$(document).ready(function () {

  /*移动端菜单*/
  $('.mobile-menu-icon').click(function () {
    $('.templatemo-left-nav').slideToggle();
  });

  /*点击 × 时弹出删除对话框*/
    $('.templatemo-content-widget .fa-times').click(function () {
        $(".mask").show(); //显示背景色
        showDialog(); //设置提示对话框的Top与Left
        $(".dialog").show(); //显示提示对话框
    });
  // 设置action属性
  var obj = null;
  var As = document.getElementsByClassName('templatemo-left-nav')[0].getElementsByTagName('a');
  for (i = 0; i < As.length; i++) {
    if (window.location.href.indexOf(As[i].href) >= 0) {
      obj = As[i];
    }
  }
  if (obj) {
    obj.setAttribute('class', 'active');
  }

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

  $(window).resize(function () { //页面窗口大小改变事件
    if (!$(".dialog").is(":visible")) {
      return;
    }
    showDialog(); //设置提示对话框的Top与Left
  });

  $("#Button3").click(function () { //注册取消按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
  });

  $("#Button2").click(function () { //注册确定按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
    $('.templatemo-content-widget:visible:first .fa-times').parent().slideUp();
  });

  $('#release-announcement').click(function () {
    $(".mask").show(); //显示背景色
    showDialog(); //设置提示对话框的Top与Left
    $(".dialog").show(); //显示提示对话框
  });

  $('#release-btn').click(function () { //注册发布按钮点击事件
    $(".dialog").hide();
    $(".mask").hide();
    if ($("#announcement-form")) {
      $("#announcement-form").submit();
    }
    if ($("#addmember-form")) {
      $("#addmember-form").submit();
    }
    if ($("#addcourse-form")) {
      $("#addcourse-form").submit();
    }
  });

  $('.edit-member').click(function () {
    var bj = $(this).html();
    if (bj == "编辑") {
      var tr = $(this).parent('td').parent('tr');
      tr.find("td.edit").each(function () {
        var old = $(this).html();　　　　　　
        $(this).html("<input type='text' name='editname' class='text' value=" + old + " >");
      })
      $(this).html("保存");
    } else if (bj == '保存') {　　　　
      $('input[name=editname]').each(function () {　　　　　　
        // var old = $(this).html();　　　　　　
        var newfont = $(this).parent('td').parent('tr').children().find('input').val();　　　　　　
        $(this).parent('td').html(newfont);　　　　
      })　　　　
      $(this).html('编辑');　　
    }
  });


  $('.course-detail').click(function () {
    $(".mask").show(); //显示背景色
    showDialog(); //设置提示对话框的Top与Left
    $(".dialog").show(); //显示提示对话框
  });

  // $('.delete-member').click(function () {
  //       var tr = $(this).parent('td').parent('tr');
  //       tr.remove();
  // });
     });