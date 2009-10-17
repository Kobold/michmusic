// sets up navigation between artists and the music player
soundManager.debugMode = false;
soundManager.url = '/static/external/sm2/swf/';

// the currently playing song, if any
var current_sound = null;

function play_toggle()
{
  if (current_sound === null) {
    return;
  }
  current_sound.togglePause();
  $('#player .control')
    .text(current_sound.paused ? 'play' : 'pause');
}

// play the song at mp3_url replacing any currently playing music
//
// encapsulates creation and destruction of Sound objects to play
function play_song(mp3_url, artist, song)
{
  if (current_sound !== null) {
    current_sound.destruct();
  }

  if (soundManager.canPlayURL(mp3_url)) {
      current_sound = soundManager.createSound({
        id: 'mich_music_sound',
        url: mp3_url,
        autoPlay: true
      });
      
      $('#player .artist').text(artist);
      $('#player .song').text(song);
  }
}

// event delegate that plays mp3s when there's a click on a play
// button within the #main div
function play_click_delegate(event)
{
  var t = $(event.target);
  if (t.hasClass('play-button')) {
    var mp3_link = t.siblings('a'),
        mp3_url = mp3_link.attr('href');
    
    play_song(mp3_url, $('#artist').text(), mp3_link.text());
  }
}

$(function() {
  $('#player .control').click(play_toggle);
  $('#main').click(play_click_delegate);

  // update the main div when a new artist is selected from the list
  $('#current-artist').change(function () {
    $.get($(this).val(), {}, function(data) {
      $('#main').html(data);
    });
  });
});
