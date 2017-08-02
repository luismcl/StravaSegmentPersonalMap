@(segmentEffort:List[domain.SegmentEffort])

function initMap() {
    var labels = ["F","C","3","2","1","HC"];

    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {lat: 40.780865, lng:  -4.001679}
  });

  @for((se) <- segmentEffort){
      var marker_@se._id = new google.maps.Marker({
        position: {lat: @se.segment.end.coordinates(0), lng:  @se.segment.end.coordinates(1)},
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

