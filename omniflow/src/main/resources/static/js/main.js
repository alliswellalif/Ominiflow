// OmniFlow Enterprise JavaScript Utilities

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Dynamic Row Adder for Order Creation
    const addItemBtn = document.getElementById('btn-add-order-item');
    const itemsTableBody = document.getElementById('order-items-tbody');

    if (addItemBtn && itemsTableBody) {
        let itemIndex = itemsTableBody.querySelectorAll('tr').length;

        addItemBtn.addEventListener('click', function () {
            const productOptions = document.getElementById('product-options-template').innerHTML;
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td>
                    <select name="items[${itemIndex}].productId" class="form-select product-select" required onchange="updateRowPrice(this)">
                        <option value="">-- Choose Product --</option>
                        ${productOptions}
                    </select>
                </td>
                <td>
                    <input type="number" name="items[${itemIndex}].quantity" class="form-control item-qty" value="1" min="1" required oninput="calculateRowTotal(this)">
                </td>
                <td>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input type="number" name="items[${itemIndex}].unitPrice" class="form-control item-price" step="0.01" min="0" required oninput="calculateRowTotal(this)">
                    </div>
                </td>
                <td class="text-end fw-bold">
                    <span class="row-total">0.00</span>
                </td>
                <td class="text-center">
                    <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeRow(this)">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            `;
            itemsTableBody.appendChild(newRow);
            itemIndex++;
        });
    }
});

function updateRowPrice(selectElem) {
    const selectedOption = selectElem.options[selectElem.selectedIndex];
    const price = selectedOption.getAttribute('data-price') || '0.00';
    const row = selectElem.closest('tr');
    const priceInput = row.querySelector('.item-price');
    if (priceInput) {
        priceInput.value = parseFloat(price).toFixed(2);
        calculateRowTotal(priceInput);
    }
}

function calculateRowTotal(elem) {
    const row = elem.closest('tr');
    const qty = parseFloat(row.querySelector('.item-qty').value) || 0;
    const price = parseFloat(row.querySelector('.item-price').value) || 0;
    const total = qty * price;
    row.querySelector('.row-total').textContent = total.toFixed(2);
    updateOrderSummary();
}

function removeRow(btn) {
    const row = btn.closest('tr');
    const tbody = row.closest('tbody');
    if (tbody.querySelectorAll('tr').length > 1) {
        row.remove();
        updateOrderSummary();
    } else {
        alert('An order must have at least one line item.');
    }
}

function updateOrderSummary() {
    let subtotal = 0;
    document.querySelectorAll('.row-total').forEach(function (span) {
        subtotal += parseFloat(span.textContent) || 0;
    });

    const subtotalElem = document.getElementById('summary-subtotal');
    if (subtotalElem) {
        subtotalElem.textContent = subtotal.toFixed(2);
    }

    const taxInput = document.getElementById('input-tax');
    const discountInput = document.getElementById('input-discount');
    const totalElem = document.getElementById('summary-grand-total');

    if (totalElem) {
        const tax = parseFloat(taxInput ? taxInput.value : 0) || 0;
        const discount = parseFloat(discountInput ? discountInput.value : 0) || 0;
        const grandTotal = Math.max(0, subtotal + tax - discount);
        totalElem.textContent = grandTotal.toFixed(2);
    }
}

function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function () {
        alert('Cryptographic Hash copied to clipboard: ' + text);
    });
}

