// sets up navigation between artists and the music player
soundManager.debugMode = false;
soundManager.url = '/static/external/sm2/swf/';

// the currently playing song, if any
var current_sound = null;

// sets up the player to use the song at mp3_url, replacing any
// current song
//
// encapsulates creation and destruction of Sound objects to play
function play_song(mp3_url, k)
{
  if (current_sound !== null) {
    current_sound.destruct();
  }

  if (soundManager.canPlayURL(mp3_url)) {
    current_sound = soundManager.createSound({
      id: 'mich_music_sound',
      url: mp3_url,
      autoLoad: true,
      onload: k
    });
  }
}

$(function() {
  // event delegate that plays mp3s when there's a click on a play
  // button within the #main div
  $('#main').click(function (event) {
    var t = $(event.target);
    if (t.hasClass('play-button')) {
      var mp3_link = t.siblings('a'),
      mp3_url = mp3_link.attr('href');
      
      // set up the player display
      $('#player-artist').text($('#artist').text());
      $('#player-song').text(mp3_link.text());
      $('#player-control img').attr('src', '/static/icons/loading.gif');
      
      $('#player').fadeIn('slow');
      
      play_song(mp3_url, function () {
        $('#player-control img').attr('src', '/static/icons/pause.png');
        current_sound.play();
      });
    }
  });

  $('#player-control img').click(function () {
    current_sound.togglePause();

    var state = current_sound.paused ? 'play' : 'pause';
    $('#player-control img').attr('src', '/static/icons/' + state + '.png');
  });

  // update the main div when a new artist is selected from the list
  $('#current-artist').change(function () {
    $.get($(this).val(), {}, function(data) {
      $('#main').html(data);
    });
  });
});
