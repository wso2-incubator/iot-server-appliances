/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


// There is currently a 25-token limit per Google user account. If a user account has 25 valid tokens, 
// the next authentication request succeeds, but quietly invalidates the oldest outstanding token without 
// any user-visible warning.
// more info: https://developers.google.com/accounts/docs/OAuth2
function EventsConfig(){
    this.currentLocationPatterns=["pattern", "Pattern"];//regex patterns
    this.eventImages = [{imgName:"Image1.png", patterns:["Image1Pattern","Image1 Pattern"]},
                        {imgName:"Image2.png", patterns:["Image2Pattern"]},
                        {imgName:"Image3.png", patterns:["Image3Pattern"]}];
    this.currentLocationHashTags="#any #hash #tag #like #this";//define current location identifier hashtags seperated with spaces.
    this.userTimeZone = "Colombo"; //example "Rome" "Los_Angeles" ecc...
    this.maxRows = 10; //events to shown

    this.debugMode = false; 
    this.clientId = 'YOUR_CLIENT_ID'; //choose web app client Id, redirect URI and Javascript origin set to http://localhost
    this.apiKey = 'YOUR_API_KEY'; //choose public apiKey, any IP allowed (leave blank the allowed IP boxes in Google Dev Console)
    this.userEmail = "YOUR_CALENDAR_ID"; //your calendar Id
    
    this.calName = "YOUR_CALENDAR_TITLE"; //name of calendar (write what you want, doesn't matter)
        
    this.scopes = 'https://www.googleapis.com/auth/calendar';

    this.pollingTimeInterval = 5000;

    this.updateDivTagId = "ELEMENT_ID_OF_THE_UPDATING_ELEMENT";
    this.currentLocTableDivId = "CURRENT_TABLE_ELEMENT_ID";
    this.otherLocTableDivId = "OTHER_TABLE_ELEMENT_ID";
    this.loginButtonDivId = "LOGIN_BTN_ID";
}

function EventsManager(theConfig){
    this.config = theConfig;
    this.eventUtil = new EventsUtil();
}

EventsManager.prototype = {
    constructor: EventsManager,
    /**
    * There is currently a 25-token limit per Google user account. If a user account has 25 valid tokens, 
    * the next authentication request succeeds, but quietly invalidates the oldest outstanding token without 
    * any user-visible warning.
    * more info: https://developers.google.com/accounts/docs/OAuth2
    **/

    handleClientLoad : function() {
        gapi.client.setApiKey(this.config.apiKey);
        window.setTimeout(this.checkAuth(),1);
    },


    handleAuthResult : function (authResult) {
        //console.log("authResult:");
        //console.log(authResult);
        var authorizeButton = document.getElementById(this.config.loginButtonDivId);
        if (authResult && !authResult.error) {
          //hide login and do the API call
          authorizeButton.style.display = "none";
          //makeApiCall()
          var self = this;
          self.makeApiCall(self);
          var readEvents=setInterval(function () {self.makeApiCall(self)}, this.config.pollingTimeInterval);
        } else {
          //show login
          authorizeButton.style.display = "block";
          authorizeButton.onclick = this.handleAuthClick();
        }
    },

    checkAuth : function () {
        //immediate: true -> the token is refreshed behind the scenes, and no UI is shown to the user.
        //console.log("checkAuth()");
        gapi.auth.authorize({client_id: this.config.clientId, scope: this.config.scopes, immediate: true}, this.handleAuthResult.bind(this));
    },


    handleAuthClick : function (event) {
        //console.log("handleAuthClick()");
        gapi.auth.authorize({client_id: this.config.clientId, scope: this.config.scopes, immediate: false}, this.handleAuthResult.bind(this));
        return false;
    },

    makeApiCall : function (context) {
        //console.log("makeApiCall");
        var today = new Date(); //today date
        gapi.client.load('calendar', 'v3', function () {
            var request = gapi.client.calendar.events.list({
                'calendarId' : context.config.userEmail,
                'timeZone' : context.config.userTimeZone, 
                'singleEvents': true, 
                'timeMin': today.toISOString(), //gathers only events not happened yet
                'maxResults': context.config.maxRows, 
                'orderBy': 'startTime'});
        request.execute(function (resp) {
                if(!resp.items) return;
                if(resp.accessRole == "freeBusyReader"){
                    //console.log("refreshing token...");
                    context.checkAuth();//try refreshing token
                    return;
                }

                //caching events ids
                var newEventsIdsArray=[[]];
                for (var i = 0; i < resp.items.length; i++) {
                        newEventsIdsArray[i][0] = resp.items[i].id;
                        newEventsIdsArray[i][1] = resp.items[i].updated;
                }
                if(this.eventsIdsArray){
                    console.log(newEventsIdsArray);
                    console.log(this.eventsIdsArray);
                    if(newEventsIdsArray.length != this.eventsIdsArray.length){
                        console.log("lengths not equal");
                        //do nothing, let it go
                    }else{
                        for (var i = 0; i < this.eventsIdsArray.length; i++) {
                            if(newEventsIdsArray[i][0] != this.eventsIdsArray[i][0] || newEventsIdsArray[i][1] != this.eventsIdsArray[i][1]){
                                console.log("ids or updated not equal");
                                break;//new events exits, let it go
                            }
                        }
                        console.log("no new events");
                        return;//no new events get outta here...
                    }
                }
                
                this.eventsIdsArray = newEventsIdsArray;

                context.clearTables(context);
                //console.log(resp);

                var count=0;
                var currentTableCount = 0;
                var otherTableCount= 0;
                
                if(context.config.debugMode)console.log(resp.items.length+" events received");
                //console.log(resp.items);

                for (var i = 0; i < resp.items.length; i++) {
                    var item = resp.items[i];
                    var classes = [];
                    var allDay = item.start.date? true : false;
                    var startDT = allDay ? item.start.date : item.start.dateTime;
                    var startDateTime = startDT.split("T"); //split date from time
                    //
                    var allDay2 = item.end.date? true : false;
                    var endDT = allDay2 ? item.end.date : item.end.dateTime;
                    var endDateTime = endDT.split("T"); //split date from time
                    //
                    var date = startDateTime[0].split("-"); //split yyyy mm dd
                    var edate = endDateTime[0].split("-"); //split yyyy mm dd
                    var startYear = date[0];
                    var startMonth = context.eventUtil.monthString(date[1]);
                    var startDay = date[2];
                    var endDay = edate[2];
                    var startDateISO = new Date(startMonth + " " + startDay + ", " + startYear + " 00:00:00");
                    var startDayWeek = context.eventUtil.dayString(startDateISO.getDay());
                    //startDay = context.eventUtil.ordinal_suffix_of(startDay); //adding st, nd, rd, th
                    var currentTable;
                    //
                    console.log(item);
                    //console.log(endDay);
                    //console.log(startDay);
                    if(context.config.debugMode)console.log("==="+item.summary);
                    var hasTagMatched = false;
                    if(item.location){
                        hashtags= item.location.match(/#[a-z0-9]*\b|\b#[a-z0-9]*$/g);//get all hashtags
                        if(hashtags){
                            if(context.config.debugMode)console.log("hastags found "+hashtags);
                            for(var k=0;k < hashtags.length;k++){
                                if(context.eventUtil.isCurrentLocationHashTag(hashtags[k], context)){
                                    hasTagMatched = true;
                                    if(context.config.debugMode)console.log("matched "+hashtags[k]+" !!!");
                                }
                            }
                        }
                    }
                    if(hasTagMatched || context.eventUtil.isCurrentLocation(item.location, context)){
                        currentTable=true;
                        currentTableCount +=1;
                        count = currentTableCount;
                        if(context.config.debugMode)console.log(item.summary + ": showing on current table");
                    }else{
                        currentTable=false;
                        otherTableCount +=1;
                        count = otherTableCount;
                        if(context.config.debugMode)console.log(item.summary + ": showing on other table");
                    }

                    if(currentTableCount % 2 != 0 && currentTable==true){
                        spcClass = "grey-bg";
                    }else if(otherTableCount % 2 != 0 && currentTable==false){
                        spcClass = "grey-bg";
                    }else{
                        spcClass = "";
                    }

                    if(startDay != endDay && allDay2 == false){
                        dayString = startDay + "-" + endDay;
                    }else{
                        dayString = startDay;
                    }

                    //
                    var location = item.location;
                    if( allDay == true){ //change this to match your needs
                        var placeStr = context.eventUtil.getLocationStr(item.location);
                        var str = [
                        '<div class="col-row border-bottom float-left ',spcClass,'">',
                            '<div class="col-date font-medium float-left padding medium-weight center">',startMonth,'<br/>', dayString,'</div>',
                            '<div class="col-image font-medium float-left padding"> <img src="images/',context.eventUtil.getEventImage(item.summary, context),'"></div>',
                            '<div class="col-summary font-medium float-left padding ">',item.summary,'</div>',
                            '<div class="col-place font-medium float-left padding center medium-weight">',placeStr,'</div>',
                            '<div class="col-time font-medium float-left padding medium-weight text-right">','All Day','</div>',
                        '</div>'
                        ];
                    }
                    else{
                        var stime = startDateTime[1].split(":"); //split hh ss etc...
                        var startTime = context.eventUtil.AmPm(stime[0],stime[1]);
                        //
                        var etime = endDateTime[1].split(":"); //split hh ss etc...
                        var endTime = context.eventUtil.AmPm(etime[0],etime[1]);

                        var placeStr = context.eventUtil.getLocationStr(item.location);
                        var str = [ //change this to match your needs
                        '<div class="col-row border-bottom float-left ',spcClass,'">',
                            '<div class="col-date font-medium float-left padding medium-weight center">',startMonth,'<br/>', dayString,'</div>',
                            '<div class="col-image font-medium float-left padding"> <img src="images/',context.eventUtil.getEventImage(item.summary, context),'"></div>',
                            '<div class="col-summary font-medium float-left padding ">',item.summary,'</div>',
                            '<div class="col-place font-medium float-left padding center medium-weight">',placeStr,'</div>',
                            '<div class="col-time font-medium float-left padding medium-weight text-right">',startTime, '<br/>', endTime,'</div>',
                        '</div>'
                            ];
                    }
                    var tdStr = str.join('');
                    if(currentTable){
                        $('#'+context.config.currentLocTableDivId).append(tdStr);
                    }else{
                        $('#'+context.config.otherLocTableDivId).append(tdStr);
                    }
                }
            //document.getElementById(context.config.updateDivTagId).innerHTML = "updated " + today;
            //document.getElementById('calendar').innerHTML = calName;
            });
        });
    },

    clearTables : function (context){

        var elmTable = document.getElementById(context.config.currentLocTableDivId);
        elmTable.innerHTML="";
        //
        elmTable = document.getElementById(context.config.otherLocTableDivId);
        elmTable.innerHTML="";
    }
}

function EventsUtil(){

     //matching current location hashtags with regex with @param locationHashTag
    this.isCurrentLocationHashTag = function(locationHashTag, context){
        //for events names you can use hashtags, eg. LK Event #palmgrove #trace #lk
        var regex = new RegExp("("+locationHashTag+")\\b"+"|"+"\\b("+locationHashTag+")$");
        return regex.test(context.config.currentLocationHashTags);
    }

    //matching regex patterns for @param location
    this.isCurrentLocation = function (location, context){
        if(!location) return false;//if null return false
        for (var i = context.config.currentLocationPatterns.length - 1; i >= 0; i--) {
            var regex = new RegExp(context.config.currentLocationPatterns[i], "i");
            if(regex.test(location)) return true;
        };
        return false;
    }

     //matching regex patterns for @param location
    this.getEventImage = function (message, context){
        for (var i = context.config.eventImages.length - 1; i >= 0; i--) {
            for (var j = context.config.eventImages[i].patterns.length - 1; j >= 0; j--) {
                var regex = new RegExp(context.config.eventImages[i].patterns[j], "i");
                //console.log(context.config.eventImages[i].patterns[j]);
                if(regex.test(message)) return context.config.eventImages[i].imgName;
            }
        };
        return "other.png"
    }

    this.getLocationStr = function (location){
        if(!location){
            return "N/A";
        }else if(location.indexOf("LK <sup>1st</sup> fl Meeting Room")>-1){
            return "#20 1<sup>st</sup> floor<br/> MeetingRoom";
        }else if(location.indexOf("LK Ground Floor Lobby Area")>-1){
            return "#20 Ground floor<br/> Lobby Area";
        }else if(location.indexOf("US 787 Castro Board Room")>-1){
            return "#US 787 Castro BoardRoom";
        }else if(location.indexOf("US Palo Alto Conference")>-1){
            return "#US Palo Alto Conf.";
        }else if(location.indexOf("LK Trace Mezzanine Floor Meeting Room")>-1){
            return "#Trace Mezzanine<br/> MeetingRoom";
        }else if(location.indexOf("LK Trace Ground Floor Training Room")>-1){
            return "#20 Ground floor<br/> TrainingRoom";
        }else if(location.indexOf("LK Trace Ground Floor Meeting Room")>-1){
            return "#20 Ground floor<br/>MeetingRoom";
        }else if(location.indexOf("LK 7th Floor Training Room")>-1){
            return "#20 7<sup>nd</sup> floor<br/> TrainingRoom";
        }else if(location.indexOf("LK 7th Floor Meeting Room")>-1){
            return "#20 7<sup>th</sup> floor<br/> MeetingRoom";
        }else if(location.indexOf("LK 6th Floor Meeting Room")>-1){
            return "#20 6<sup>th</sup> floor<br/> MeetingRoom";
        }else if(location.indexOf("LK 5th Floor Meeting Room")>-1){
            return "#20 5<sup>th</sup> floor<br/> MeetingRoom";
        }else if(location.indexOf("LK 4th Floor Meeting Room")>-1){
            return "#20 4<sup>th</sup> floor<br/> MeetingRoom";
        }else if(location.indexOf("LK 3rd Floor Training Room")>-1){
            return "#20 3<sup>rd</sup> floor<br/> BoilerRoom";
        }else if(location.indexOf("LK 3rd Floor Meeting Room")>-1){
            return "#20 3<sup>rd</sup> floor<br/> KernelRoom";
        }else if(location.indexOf("LK 2nd Floor Meeting Room")>-1){
            return "#20 2<sup>nd</sup> floor<br/> MeetingRoom";
        }else{
            return location;
        }
    }

    this.padNum = function (num) {
        if (num <= 9 && num==0) {
            return "0" + num;
        }
        return num;
    }

    this.AmPm =function (hour, min) {
        if (hour <= 12) { return hour+":"+min + "a.m."; }
        return this.padNum(hour - 12) + ":" + min + "p.m.";
    }

    this.AmPmOnly =function (hour, min) {
        if (hour <= 12) { return "AM"; }
        return "PM";
    }

    this.monthString =  function(num) {
             if (num === "01") { return "January"; } 
        else if (num === "02") { return "February"; } 
        else if (num === "03") { return "March"; } 
        else if (num === "04") { return "April"; } 
        else if (num === "05") { return "May"; } 
        else if (num === "06") { return "June"; } 
        else if (num === "07") { return "July"; } 
        else if (num === "08") { return "August"; } 
        else if (num === "09") { return "September"; } 
        else if (num === "10") { return "October"; } 
        else if (num === "11") { return "November"; } 
        else if (num === "12") { return "December"; }
    }

    this.dayString = function (num){
             if (num == "1") { return "Monday" }
        else if (num == "2") { return "Tuesday" }
        else if (num == "3") { return "Wednesday" }
        else if (num == "4") { return "Thursday" }
        else if (num == "5") { return "Friday" }
        else if (num == "6") { return "Saturday" }
        else if (num == "0") { return "Sunday" }
    }

    this.dayShortString = function (num){
             if (num == "1") { return "Mon" }
        else if (num == "2") { return "Tue" }
        else if (num == "3") { return "Wed" }
        else if (num == "4") { return "Thur" }
        else if (num == "5") { return "Fri" }
        else if (num == "6") { return "Sat" }
        else if (num == "0") { return "Sun" }
    }

    this.ordinal_suffix_of = function (i) {
        var j = i % 10, k = i % 100;
        if (j == 1 && k != 11) { "st"; }
        if (j == 2 && k != 12) { "nd"; }
        if (j == 3 && k != 13) { "rd"; }
        return "th";
    }

    this.getCookie = function (cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i<ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1);
            if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
        }
        return "";
    }

    this.setCookie = function (cname, cvalue, extime) {
        var d = new Date();
        d.setTime(d.getTime() + extime);
        var expires = "expires="+d.toUTCString();
        //document.cookie = cname + "=" + cvalue + "; " + expires;

        if(document.domain === 'localhost') {
            document.cookie = cname + "=" + cvalue + "; " + expires + ';path=/;' ;
        } else {
            document.cookie = cname + "=" + cvalue + "; " + expires + ';domain=.' + document.domain + ';path=/;';
        }
    }
}
