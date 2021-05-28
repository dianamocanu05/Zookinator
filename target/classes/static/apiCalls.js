function post(){
$.ajax({
    type: 'POST',
    url : "http://localhost:5432/api/send",
    contentType: "application/json",
     headers: {
        "Content-type": "application/json",
        "Access-Control-Allow-Origin": "*"
      },
    data: JSON.stringify({
        hasHair : "true",
        hasFeathers : "true",
        laysEggs : "true",
        hasMilk : "true",
        isAirborne : "true",
        isAquatic : "true",
        isPredator : "true",
        isToothed : "true",
        hasBackbone : "true",
        breathes : "true",
        isVenomous : "true",
        hasFins : "true",
        legs : "4",
        hasTail : "true",
        isDomestic : "true",
        isCatsized : "true"
    }),
     success: function(data) { alert("ajax worked"); },
     error: function(data) {alert("ajax error"); },
     dataType: 'json'
  })
}