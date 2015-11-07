$(function() {
  $("#remove").click(function(event) {
    if(confirm("Are you sure you want to delete it?")) {
      $.ajax({
        type: "DELETE",
        url: window.location.href,
        success: function(msg) {
          url = window.location.href.toString();
          url = url.replace("/" + msg, "");
          window.location.replace(url);
        }
      });
    }
  });
});
