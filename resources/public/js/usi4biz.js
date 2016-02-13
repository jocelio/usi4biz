// Usi4Biz: User Interaction For Business
// Copyright (C) 2015 Hildeberto Mendonça
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.

/* If the screen contains a button with the id "remove", then a click event
   is added to it in order to remove the current record. It works only if
   the url shows the visualization of a record. */
$("#remove").click(function(event) {
  if(confirm("Are you sure you want to delete it?")) {
    $.ajax({
      type: "DELETE",
      url: window.location.href,
      success: function(msg) {
        url = window.location.href.toString();
        url = url.replace("/" + msg, "");
        console.log(url);
        window.location.replace(url);
      }
    });
  }
});

/* If the screen contains links to remove sub records, then a click event
   is added to them in order to allow removing each sub-record. */
$("a[id^='issues-']" ).click(function(event) {
  var target = $(event.target);
  var id = target.attr("id");
  if (typeof id == 'undefined') {
    target = target.parent();
    id = target.attr("id");
  }
  if (typeof id != 'undefined') {
    link = id.replace(/-/g , "/");
    link = "/" + link;
    if(confirm("Are you sure you want to delete it?")) {
      $.ajax({
        type: "DELETE",
        url: link,
        success: function(msg) {
          url = window.location.href.toString();
          url = url.replace("#", "");
          console.log(url);
          window.location.replace(url);
        }
      });
    }
  }
});

/* When a product is selected in a select field it loads its related milestones
   in another select field. */
$("#product").on('change', function() {
  $("#milestone").find("option")
  .remove()
  .end()
  .append('<option value="">Milestones...</option>')
  .val('')
  $.ajax({
    url: "/api/milestones?product=" + $("#product").val()
  }).then(function(data) {
    $.each(data, function(key, value) {
      $('#milestone')
      .append($("<option></option>")
              .attr("value", value.id)
              .text(value.name));
    });
  });
});
