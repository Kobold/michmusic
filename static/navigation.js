// sets up navigation using the select element

$(function() {
  $('#current-artist').change(function (event) {
    $('#main').load($(this).val());
  });
});
