$(document).ready(function() {
	$('.shift-info').editableSelect({ //下拉且可输入
        effects: 'slide',
        onSelect: function(element) {
            $('.shift-info').attr('data-val', element.val(), )
        }
    })

    $('.shift-info').prop('placeholder', '请输入或选择..');
})
