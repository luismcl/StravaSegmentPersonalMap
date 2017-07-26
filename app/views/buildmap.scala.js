@(athlete:domain.Athlete)

function initMap() {
    var labels = ["F","C","3","2","1","HC"];
    var myLatLng = {lat: -25.363, lng: 131.044};

    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {lat: 40.780865, lng:  -4.001679}
  });

  @for((segmentResume) <- athlete.segments){
      new google.maps.Marker({
        position: {lat: @segmentResume.latitude, lng:  @segmentResume.longitude},
        map: map,
        title: '@segmentResume.name',
        label: labels[@segmentResume.climbCategory]
      });
  }
}
