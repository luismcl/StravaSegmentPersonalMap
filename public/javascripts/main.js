function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
}

function emptyMap() {
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {lat: 40.780865, lng:  -4.001679}
  });
}

function closeBanner(){
    var div = document.getElementById("infoBanner");
    div.style.opacity = "0";
    setTimeout(function(){ div.style.display = "none"; }, 600);
}