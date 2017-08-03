@(segmentEffort:List[domain.SegmentEffort])

var map

function initMap() {
    console.log("Build Map")
    var labels = ["F","C","3","2","1","HC"];

    var icons = {
      5: {
        icon: {
              url:"@routes.Assets.versioned("images/hctour.png")",
              scaledSize: new google.maps.Size(20, 20)
              }
      },
      4: {
        icon: {
              url:"@routes.Assets.versioned("images/1tour.png")",
              scaledSize: new google.maps.Size(20, 20)
              }
      },
      3: {
        icon: {
               url:"@routes.Assets.versioned("images/2tour.png")",
               scaledSize: new google.maps.Size(20, 20)
               }
      },
      2: {
        icon: {
            url:"@routes.Assets.versioned("images/3tour.png")",
            scaledSize: new google.maps.Size(20, 20)
        }
      }
    };


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

  var markers = new Array()

  @for((se) <- segmentEffort){
      var marker_@se._id = new google.maps.Marker({
        position: {lat: @se.segment.end.coordinates(0), lng:  @se.segment.end.coordinates(1)},
        map: map,
        title: '@se.segment.name',
        icon: icons[@se.segment.climbCategory].icon,
        scale:10
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

       markers.push(marker_@{se._id});
  }

  var markerCluster = new MarkerClusterer(map, markers,
              {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
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

