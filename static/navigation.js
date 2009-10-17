// sets up navigation between artists and the music player
soundManager.debugMode = false;
soundManager.url = '/static/external/sm2/swf/';

var STATE_INITIAL = 0;
var STATE_PLAYING = 1;
var STATE_PAUSED  = 2;

// the currently playing song, if any and the state of the player system
var current_sound = null;
var current_state = STATE_INITIAL;

var state_transitions = {};
state_transitions[[STATE_INITIAL, STATE_PLAYING]] = function () {
  $('#player').fadeIn('slow');
};
state_transitions[[STATE_PLAYING, STATE_PAUSED]] = function () {
  $('#player-control img').attr('src', '/static/icons/play.png');
  current_sound.pause();
};
state_transitions[[STATE_PAUSED, STATE_PLAYING]] = function () {
  $('#player-control img').attr('src', '/static/icons/pause.png');
  current_sound.play();
};

function transition(new_state)
{
  var st = state_transitions[[current_state, new_state]];
  if ($.isFunction(st)) { st(); }
  current_state = new_state;
}

// sets up the player to use the song at mp3_url, replacing any
// current song
//
// encapsulates creation and destruction of Sound objects to play
function initialize_song(mp3_url, artist, song)
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
      
      $('#player-artist').text($('#artist').text());
      $('#player-song').text(mp3_link.text());
      initialize_song(mp3_url);
      transition(STATE_PLAYING);
    }
  });

  $('#player-control img').click(function () {
    if (current_state == STATE_PLAYING) {
      transition(STATE_PAUSED);
    } else if (current_state == STATE_PAUSED) {
      transition(STATE_PLAYING);
    }
  });

  // update the main div when a new artist is selected from the list
  $('#current-artist').change(function () {
    $.get($(this).val(), {}, function(data) {
      $('#main').html(data);
    });
  });
});
