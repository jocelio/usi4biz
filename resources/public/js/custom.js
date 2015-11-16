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
    $("#milestone").find("option")
                   .remove()
                   .end()
                   .append('<option value="">Milestones...</option>')
                   .val('')
    $.ajax({
      url: "http://localhost:3000/api/milestones?product=" + $("#product").val()
    }).then(function(data) {
      $.each(data, function(key, value) {
        $('#milestone')
          .append($("<option></option>")
          .attr("value",key.id)
          .text(value.name));
        });
    });
  });
});
