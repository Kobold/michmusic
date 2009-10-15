// sets up navigation between artists and the music player
soundManager.url = '/static/external/sm2/swf/';

// update the main div when a new artist is selected from the list
function artist_selected(event)
{
  $.get($(this).val(), {}, function(data) {
    $('#main').html(data);
  });
}

// event delegate that plays mp3s when there's a click on a play
// button within the #main div
function play_click_delegate(event)
{
  var t = $(event.target);
  if (t.hasClass('play-button')) {
    var mp3_url = t.siblings('a').attr('href');
    
    if (soundManager.canPlayURL(mp3_url)) {
      soundManager.createSound({
        id: 'fooSound',
        url: mp3_url,
        autoPlay: true
      });
    }
  }
}

$(function() {
  $('#current-artist').change(artist_selected);
  $('#main').click(play_click_delegate);
});
