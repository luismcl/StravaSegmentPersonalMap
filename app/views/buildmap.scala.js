@(segmentEffort:List[domain.SegmentEffort])

var map

function initMap() {
    console.log("Build Map")
    var labels = ["F","C","3","2","1","HC"];

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8
  });

    console.log("City:"+document.getElementById('cityValue').textContent)
    var request = {
        query: document.getElementById('cityValue').textContent
      };

    service = new google.maps.places.PlacesService(map);
    service.textSearch(request, placeCallback);

  document.getElementById("totalSegments").textContent="@segmentEffort.size";

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

function placeCallback(results, status) {
    console.log("placeCallback!!!!!!!"+status)
  if (status == google.maps.places.PlacesServiceStatus.OK) {
      if (results.length > 0) {
          var place = results[0];
          map.setCenter(place.geometry.location)
      }else{
         map.setCenter({lat: 0.780865, lng:  -4.001679})
      }
  }
}

