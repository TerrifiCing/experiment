$(document).ready(function() {

/*移动端菜单*/
$('.mobile-menu-icon').click(function() {
    $('.templatemo-left-nav').slideToggle();
});

/*点击 × 时弹出删除对话框*/
$('.templatemo-content-widget .fa-times').click(function() {
    $(".mask").show(); //显示背景色
    showDialog(); //设置提示对话框的Top与Left
    $(".dialog").show(); //显示提示对话框
});

function showDialog() {
    var objW = $(window); //当前窗口
    var objC = $(".dialog"); //对话框
    var brsW = objW.width();
    var brsH = objW.height();
    var sclL = objW.scrollLeft();
    var sclT = objW.scrollTop();
    var curW = objC.width();
    var curH = objC.height();
    //计算对话框居中时的左边距
    var left = sclL + (brsW - curW) / 2;
    //计算对话框居中时的上边距
    var top = sclT + (brsH - curH) / 2;
    //设置对话框在页面中的位置
    objC.css({
        "left": left,
        "top": top
    });
};

$(window).resize(function() { //页面窗口大小改变事件
    if (!$(".dialog").is(":visible")) {
        return;
    }
    showDialog(); //设置提示对话框的Top与Left
});

$("#Button3").click(function() { //注册取消按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
});

$("#Button2").click(function() { //注册确定按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
    $('.templatemo-content-widget:visible:first .fa-times').parent().slideUp();
});

$('#release-announcement').click(function() {
    $(".mask").show(); //显示背景色
    showDialog(); //设置提示对话框的Top与Left
    $(".dialog").show(); //显示提示对话框
});

$('#release-btn').click(function() { //注册发布按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
    if ($("#announcement-form")) {
        $("#announcement-form").submit();
    }
    if ($("#addmember-form")) {
        $("#addmember-form").submit();
    }
});

$('.edit-member').click(function() {
    var bj = $(this).html();
    if (bj == "编辑") {
        var tr = $(this).parent('td').parent('tr');
        tr.find("td.edit").each(function() {
            var old = $(this).html();　　　　　　
            $(this).html("<input type='text' name='editname' class='text' value=" + old + " >");
        })
        $(this).html("保存");
    } else if (bj == '保存') {　　　　
        $('input[name=editname]').each(function() {　　　　　　
            // var old = $(this).html();　　　　　　
            var newfont = $(this).parent('td').parent('tr').children().find('input').val();　　　　　　
            $(this).parent('td').html(newfont);　　　　
        })　　　　
        $(this).html('编辑');　　
    }
});

$('.delete-member').click(function() {
    var tr = $(this).parent('td').parent('tr');
    tr.remove();
});

$('.add-course').click(function() {
    var last_tr = $('table tbody tr:last');
    var new_tr = "<tr><td class='edit'></td><td class='edit'></td><td class='edit'></td><td class='edit'></td><td class='edit'></td><td width='200'><button class='templatemo-edit-btn edit-member' type='button'>编辑</button>&nbsp;<button class='templatemo-delete-btn delete-member' type='button'>删除</button></td></tr>"
    last_tr.after(new_tr);
    $('.edit-member').click(function() {
        var bj = $(this).html();
        if (bj == "编辑") {
            var tr = $(this).parent('td').parent('tr');
            tr.find("td.edit").each(function() {
                var old = $(this).html();　　　　　　
                $(this).html("<input type='text' name='editname' class='text' value=" + old + " >");
            })
            $(this).html("保存");
        } else if (bj == '保存') {　　　　
            $('input[name=editname]').each(function() {　　　　　　
                // var old = $(this).html();　　　　　　
                var newfont = $(this).parent('td').parent('tr').children().find('input').val();　　　　　　
                $(this).parent('td').html(newfont);　　　　
            })　　　　
            $(this).html('编辑');　　
        }
    })

    $('.delete-member').click(function() {
        var tr = $(this).parent('td').parent('tr');
        tr.remove();
    });
});

$("#oldpassword").blur(function() {
    var param = $("#oldpassword").val();
    $.ajax({
        url: "${HttpPath}/user/checkoldpassword",
        data: {
            oldpassword: param
        },
        success: function(e) {
            if (e.code == 1) {
                $("#tip1").html("<font color=\"green\" size=\"2\"> 正确</font>");
            } else {
                $("#tip1").html("<font color=\"red\" size=\"2\">  错误</font>");
            }
        }
    });
});
$("#password1").blur(function() {
    var num = $("#password1").val().length;
    if (num < 6) {
        $("#tip2").html("<font color=\"red\" size=\"2\">  密码太短，不应少于6个字符</font>");
    } else if (num > 18) {
        $("#tip2").html("<font color=\"red\" size=\"2\">  密码太长，不应多于18个字符</font>");
    } else {
        $("#tip2").html("<font color=\"green\" size=\"2\"> 正确</font>");
    }
});
$("#password2").blur(function() {
    var tmp = $("#password1").val();
    var num = $("#password2").val().length;
    if ($("#password2").val() != tmp) {
        $("#tip3").html("<font color=\"red\" size=\"2\">  错误</font>");
    } else {
        if (num >= 6 && num <= 18) {
            $("#tip3").html("<font color=\"green\" size=\"2\">  正确</font>");
        } else {
            $("#tip3").html("<font color=\"red\" size=\"2\">  无效</font>");
        }
    }
});
$("#btn").click(function() {
    var flag = true;
    var old = $("#oldpassword").val();
    var pass = $("#password1").val();
    var pass2 = $("#password2").val();
    var num1 = $("#password1").val().length;
    var num2 = $("#password2").val().length;
    if (num1 != num2 || num1 < 6 || num2 < 6 || num1 > 18 || num2 > 18 || pass != pass2) {
        flag = false;
    } else {
        flag = true;
    }
    if (flag) {
        $.ajax({
            url: "${HttpPath}/user/editPassowrdyet",
            data: {
                oldpassword: old,
                password: pass
            },
            success: function(e) {
                if (e.code == 1) {
                    $("#tip4").show().html("<font color=\"green\" size=\"3\">  修改成功!</font>");
                    $("#oldpassword").val("");
                    $("#password1").val("");
                    $("#password2").val("");
                    $("#tip1").empty();
                    $("#tip2").empty();
                    $("#tip3").empty();
                    $("#tip4").delay(2000).hide(0);
                } else {
                    $("#tip4").show().html("<font color=\"red\" size=\"3\">  原密码输入错误!</font>");
                }
            }
        });
    } else {

        $("#tip4").show().html("<font color=\"red\" size=\"3\">  请按要求输入!</font>");
    }
});

});
