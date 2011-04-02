/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markupartist.sthlmtraveling.provider.planner;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.markupartist.sthlmtraveling.provider.planner.Planner.Location;

public class JourneyQuery implements Parcelable {
    public Location origin;
    public Location destination;
    public Time time;
    public boolean isTimeDeparture;
    public boolean alternativeStops;
    public ArrayList<String> transportModes;
    public String ident;
    public String seqnr;

    public JourneyQuery() {
    }

    public JourneyQuery(Parcel parcel) {
        origin = parcel.readParcelable(Location.class.getClassLoader());
        destination = parcel.readParcelable(Location.class.getClassLoader());
        time = new Time();
        time.parse(parcel.readString());
        isTimeDeparture = (parcel.readInt() == 1) ? true : false;
        alternativeStops = (parcel.readInt() == 1) ? true : false;
        transportModes = new ArrayList<String>();
        parcel.readStringList(transportModes);
        ident = parcel.readString();
        seqnr = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(origin, 0);
        dest.writeParcelable(destination, 0);
        dest.writeString(time.format2445());
        dest.writeInt(isTimeDeparture ? 1 : 0);
        dest.writeInt(alternativeStops ? 1 : 0);
        dest.writeStringList(transportModes);
        dest.writeString(ident);
        dest.writeString(seqnr);
    }

    public static final Creator<JourneyQuery> CREATOR = new Creator<JourneyQuery>() {
        public JourneyQuery createFromParcel(Parcel parcel) {
            return new JourneyQuery(parcel);
        }

        public JourneyQuery[] newArray(int size) {
            return new JourneyQuery[size];
        }
    };

    public JSONObject toJson() throws JSONException {
        return toJson(true);
    }

    public JSONObject toJson(boolean all) throws JSONException {
        JSONObject jsonOrigin = new JSONObject();
        jsonOrigin.put("id", origin.id);
        jsonOrigin.put("name", origin.name);
        jsonOrigin.put("latitude", origin.latitude);
        jsonOrigin.put("longitude", origin.longitude);

        JSONObject jsonDestination = new JSONObject();
        jsonDestination.put("id", destination.id);
        jsonDestination.put("name", destination.name);
        jsonDestination.put("latitude", destination.latitude);
        jsonDestination.put("longitude", destination.longitude);

        JSONObject jsonQuery = new JSONObject();

        if (transportModes != null) {
            jsonQuery.put("transportModes", new JSONArray(transportModes));
        }

        jsonQuery.put("alternativeStops", alternativeStops);
        jsonQuery.put("origin", jsonOrigin);
        jsonQuery.put("destination", jsonDestination);

        if (all) {
            jsonQuery.put("ident", ident);
            jsonQuery.put("seqnr", seqnr);
            jsonQuery.put("time", time.format("%F %R"));
            jsonQuery.put("isTimeDeparture", this.isTimeDeparture);
        }

        return jsonQuery;
    }
    
    public static class Builder {
        private Planner.Location mOrigin;
        private Planner.Location mDestination;
        private Time mTime;
        private boolean mIsTimeDeparture;
        private boolean mAlternativeStops;
        private ArrayList<String> mTransportModes;        

        public Builder() {
            
        }

        public Builder origin(Stop origin) {
            mOrigin = buildLocationFromStop(origin);
            return this;
        }

        public Builder destination(Stop destination) {
            mDestination = buildLocationFromStop(destination);
            return this;
        }

        public Builder time(Time time) {
            mTime = time;
            return this;
        }

        public Builder isTimeDeparture(boolean isTimeDeparture) {
            mIsTimeDeparture = isTimeDeparture;
            return this;
        }

        public Builder alternativeStops(boolean alternativeStops) {
            mAlternativeStops = alternativeStops;
            return this;
        }

        public Builder transportModes(ArrayList<String> transportModes) {
            mTransportModes = transportModes;
            return this;
        }

        public JourneyQuery create() {
            JourneyQuery journeyQuery = new JourneyQuery();
            journeyQuery.origin = mOrigin;
            journeyQuery.destination = mDestination;

            if (mTime == null) {
                mTime = new Time();
                mTime.setToNow();
            }
            journeyQuery.time = mTime;
            journeyQuery.isTimeDeparture = mIsTimeDeparture;
            journeyQuery.alternativeStops = mAlternativeStops;
            journeyQuery.transportModes = mTransportModes;

            return journeyQuery;
        }

        private Planner.Location buildLocationFromStop(Stop stop) {
            Planner.Location location = new Planner.Location();
            location.id = stop.getSiteId();
            location.name = stop.getName();
            if (stop.getLocation() != null) {
                location.latitude =
                    (int) (stop.getLocation().getLatitude() * 1E6);
                location.longitude =
                    (int) (stop.getLocation().getLongitude() * 1E6);
            }    
            return location;
        }
    }
}
