@(segmentEffort:List[domain.SegmentEffort])

function initMap() {
    var labels = ["F","C","3","2","1","HC"];
    var myLatLng = {lat: -25.363, lng: 131.044};

    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {lat: 40.780865, lng:  -4.001679}
  });

  @for((se) <- segmentEffort){
      var marker_@se._id = new google.maps.Marker({
        position: {lat: @se.segment.start.coordinates(0), lng:  @se.segment.start.coordinates(1)},
        map: map,
        title: '@se.segment.name',
        label: labels[@se.segment.climbCategory]
      });

       marker_@{se._id}.addListener('click', function(){
       showInfoWindow(marker_@se._id,
                      '@se.segment.name',
                      '@se.segment.distance',
                      '@se.segment.averageGrade',
                      labels[@se.segment.climbCategory],
                      '@se._id',
                      '@se.lastDate',
                      '@se.elapsedTime'
       )});
  }
}

function showInfoWindow(marker, name, distance,averageGrade,climbCategory,segmentEffortId,lastDate,elapsedTime){
        var infowindow = new google.maps.InfoWindow()
        var contentString = '<div id="content">'+
                    '<div id="siteNotice">'+
                    '</div>'+
                    '<h1 id="firstHeading" class="firstHeading">'+name+'</h1>'+
                    '<div id="bodyContent">'+
                    '<p><b>Distance:</b> ' + (distance/1000).toFixed(1)  +' KM</p>' +
                    '<p><b>Average Grade:</b> ' + parseFloat(averageGrade).toFixed(0) +'%</p>' +
                    '<p><b>Climb Category:</b> ' + climbCategory +'</p>' +
                    '<p><b>Last visited:</b> ' + lastDate +'</p>' +
                    '<p><b>Last Time:</b> ' + elapsedTime.toHHMMSS() +'</p>' +
                    '<p><a target="_blank" href="https://www.strava.com/segment_efforts/'+segmentEffortId+'">Show Effort</a></p>'+
                    '</div>'+
                    '</div>';
        infowindow.setContent(contentString);
        infowindow.open(map, marker);
    }

String.prototype.toHHMMSS = function () {
    var sec_num = parseInt(this, 10); // don't forget the second param
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours   < 10) {hours   = "0"+hours;}
    if (minutes < 10) {minutes = "0"+minutes;}
    if (seconds < 10) {seconds = "0"+seconds;}
    return hours+':'+minutes+':'+seconds;
}