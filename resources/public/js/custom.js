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

  $("#product").on('change', function() {
    selectValues = { "1": "test 1", "2": "test 2" };
    $.each(selectValues, function(key, value) {
      $('#milestone')
        .append($("<option></option>")
        .attr("value",key)
        .text(value));
      });
  });
});
