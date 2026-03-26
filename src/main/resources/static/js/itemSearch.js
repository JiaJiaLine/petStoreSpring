// 记得包裹在 ready 函数中，确保页面加载完再绑定事件
$(document).ready(function() {
    // 当 category 的下拉框发生改变时触发
    $('#categorySelect').change(function() {
        var categoryId = $(this).val(); // 拿到选中的分类 ID (比如 "DOGS")
        var productSelect = $('#productSelect'); // 找到第二个下拉框

        // 先清空第二个下拉框的旧数据
        productSelect.empty();
        productSelect.append('<option value="">-- Select Product --</option>');

        // 如果用户真的选了一个分类，就发起 AJAX 请求
        if(categoryId) {
            // 向刚才写的 Controller 接口发请求
            $.get('/manage/getProductsByCategory', {categoryId: categoryId}, function(data) {
                // data 就是后端传过来的 JSON 数组（产品列表）
                var options = ' '; // 先定义一个字符串
                // 把查出来的每一个产品，变成一个 <option> 塞进第二个下拉框
                $.each(data, function(index, product) {
                    // 不停地往这个长字符串后面累加
                    options += '<option value="'+product.productId+'">'+product.name+'</option>';
                });
// 循环结束后，只调用一次 append，对网页的伤害最小
                productSelect.append(options);
            });
        }
    });
});