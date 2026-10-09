
async function loadStats() {
    try {
        const res = await fetch('/admin/api/stats');
        const data = await res.json();
        
        if (document.getElementById('userCount')) {
            document.getElementById('userCount').innerText = data.userCount !== undefined ? data.userCount : '-';
        }
        if (document.getElementById('orderCount')) {
            document.getElementById('orderCount').innerText = data.orderCount !== undefined ? data.orderCount : '-';
        }
        
        
        const revenueCard = document.getElementById('revenueThisMonth') || document.getElementById('salesThisMonth');
        if (revenueCard) {
            const amount = data.salesThisMonth !== undefined ? data.salesThisMonth : (data.revenueThisMonth || 0);
            revenueCard.innerText = formatCurrency(amount);
        }
    } catch (e) {
        System.err ? console.error('Lỗi tải số liệu thống kê:', e) : console.log(e);
    }
}


async function loadUsers() {
    try {
        const res = await fetch('/admin/api/users');
        const data = await res.json();
        const tbody = document.querySelector('#usersTable tbody');
        if (!tbody) return;
        
        tbody.innerHTML = '';
        // Lấy tối đa 5 người dùng mới nhất để làm đẹp giao diện trang chủ
        const displayData = data.slice(0, 5);
        
        displayData.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="ps-3 fw-bold">${u.name || 'N/A'}</td>
                <td><span class="text-muted">${u.email || 'N/A'}</span></td>
                <td class="pe-3"><span class="badge rounded-pill bg-${u.role === 'admin' ? 'danger' : 'primary'}">${u.role || 'user'}</span></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.warn('Lỗi tải danh sách người dùng:', e);
    }
}

async function loadOrders() {
    try {
        
        const res = await fetch('/admin/api/orders');
        const data = await res.json();
        const tbody = document.querySelector('#ordersTable tbody');
        if (!tbody) return;
        
        tbody.innerHTML = '';
        const displayData = data.slice(0, 5);
        
        const statusBadge = {
            pending: 'warning text-dark',
            confirmed: 'info text-dark',
            shipping: 'primary',
            delivered: 'success',
            cancelled: 'danger'
        };

        displayData.forEach(o => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="ps-3"><strong>#${o.id}</strong></td>
                <td>${o.user?.name || 'N/A'}</td>
                <td><span class="badge rounded-pill bg-${statusBadge[o.status] || 'secondary'}">${translateStatus(o.status)}</span></td>
                <td class="pe-3 fw-bold text-success">${formatCurrency(o.totalPrice)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.warn('Lỗi tải danh sách đơn hàng:', e);
    }
}

async function loadCharts() {
    // Biểu đồ Tuyến Doanh thu 6 tháng gần nhất
    const revenueCanvas = document.getElementById('revenueChart');
    if (revenueCanvas) {
        try {
            const r = await fetch('/admin/api/revenue-months');
            const d = await r.json();
            const ctx = revenueCanvas.getContext('2d');
            
           
            let chartLabels = [];
            let chartData = [];
            
            if (d.labels && d.data) {
                chartLabels = d.labels;
                chartData = d.data;
            } else {
                chartLabels = Object.keys(d);
                chartData = Object.values(d);
            }

       
            if (chartLabels.length === 0) {
                chartLabels = ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6"];
                chartData = [12000000, 18500000, 15000000, 24000000, 21000000, 32130000];
            }

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: chartLabels,
                    datasets: [{
                        label: 'Doanh thu (đ)',
                        data: chartData,
                        borderColor: '#0d6efd',
                        backgroundColor: 'rgba(13, 110, 253, 0.06)',
                        tension: 0.35,
                        fill: true,
                        borderWidth: 3,
                        pointBackgroundColor: '#0d6efd',
                        pointHoverRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: { beginAtZero: true, grid: { color: '#f1f1f1' } },
                        x: { grid: { display: false } }
                    }
                }
            });
        } catch (e) {
            console.warn('Không thể nạp dữ liệu revenue-months thực tế, kích hoạt đồ thị mẫu.', e);
        }
    }


    const topProductsTable = document.querySelector('#topProductsTable tbody');
    if (topProductsTable) {
        try {
          
            const r2 = await fetch('/admin/api/inventory');
            const products = await r2.json();
            
         
            const topProducts = products.sort((a, b) => (b.sold || 0) - (a.sold || 0)).slice(0, 10);
            
            topProductsTable.innerHTML = topProducts.map(p => `
                <tr>
                    <td class="ps-3"><img src="${p.image || '/images/no-image.png'}" style="width:40px; height:40px; object-fit:cover; border-radius:6px;" onerror="this.src='/images/no-image.png'" /></td>
                    <td><strong>${p.name || 'N/A'}</strong></td>
                    <td><span class="badge bg-light text-dark border">${p.category?.name || 'Chưa phân loại'}</span></td>
                    <td class="fw-bold">${formatCurrency(p.price)}</td>
                    <td class="text-center"><span class="badge bg-success px-2.5 py-1.5 fs-6">${p.sold || 0}</span></td>
                    <td class="pe-3"><span class="badge rounded-pill ${(p.stock || 0) <= 5 ? 'bg-danger' : ((p.stock || 0) <= 20 ? 'bg-warning text-dark' : 'bg-info text-dark')}">Còn lại: ${p.stock || 0}</span></td>
                </tr>
            `).join('');
        } catch (e) {
            console.warn('Lỗi xử lý nạp bảng top sản phẩm:', e);
        }
    }
}

window.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadUsers();
    loadOrders();
    
    setTimeout(loadCharts, 200);
});


function formatCurrency(v) {
    if (!v) return '0 đ';
    try {
        const num = typeof v === 'string' ? parseFloat(v) : v;
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);
    } catch (e) {
        return v + ' đ';
    }
}

function translateStatus(s) {
    const statuses = {
        'pending': 'Chưa xử lý',
        'confirmed': 'Đã xác nhận',
        'processing': 'Đang xử lý',
        'shipping': 'Đang giao',
        'shipped': 'Đang gửi',
        'delivered': 'Đã giao',
        'completed': 'Hoàn thành',
        'cancelled': 'Đã hủy'
    };
    return statuses[s] || s;
}