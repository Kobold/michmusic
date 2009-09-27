// sets up navigation using the select element

$(function() {
  $('#current-artist').change(function (event) {
    window.location = $(this).val();
  });
});
