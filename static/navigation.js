// sets up navigation between artists and the music player

var oneBit = new OneBit('/static/external/1bit.swf');
oneBit.specify('playerSize', '11');

function artist_selected(event) {
  $.get($(this).val(), {}, function(data) {
    $('#main').html(data);
    oneBit.apply('a');
  });
};

$(function() {
  $('#current-artist').change(artist_selected);
});
