package org.osmdroid.bonuspack.kml;

import java.io.IOException;
import java.io.Writer;
import org.osmdroid.bonuspack.kml.KmlFeature.Styler;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * KML and/or GeoJSON LineString
 * @author M.Kergall
 */
public class KmlLineString extends KmlGeometry {
	
	public KmlLineString(){
		super();
	}
	
	public KmlLineString(JsonObject json){
		this();
		JsonArray coordinates = json.get("coordinates").getAsJsonArray();
		mCoordinates = KmlGeometry.parseGeoJSONPositions(coordinates);
	}
	
	public void applyDefaultStyling(Polyline lineStringOverlay, Style defaultStyle, KmlPlacemark kmlPlacemark,
			KmlDocument kmlDocument){
		Style style = kmlDocument.getStyle(kmlPlacemark.mStyle);
		if (style != null){
			lineStringOverlay.setPaint(style.getOutlinePaint());
		} else if (defaultStyle!=null && defaultStyle.mLineStyle!=null){ 
			lineStringOverlay.setPaint(defaultStyle.getOutlinePaint());
		}
		lineStringOverlay.setEnabled(kmlPlacemark.mVisibility);
	}
	
	/** Build the corresponding Polyline overlay */	
	@Override public Overlay buildOverlay(MapView map, Style defaultStyle, Styler styler, KmlPlacemark kmlPlacemark, 
			KmlDocument kmlDocument){
		Context context = map.getContext();
		Polyline lineStringOverlay = new Polyline(context);
		lineStringOverlay.setPoints(mCoordinates);
		if (styler != null)
			styler.onLineString(lineStringOverlay, kmlPlacemark, this);
		else {
			applyDefaultStyling(lineStringOverlay, defaultStyle, kmlPlacemark, kmlDocument);
		}
		return lineStringOverlay;
	}
	
	@Override public void saveAsKML(Writer writer){
		try {
			writer.write("<LineString>\n");
			writeKMLCoordinates(writer, mCoordinates);
			writer.write("</LineString>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override public JsonObject asGeoJSON(){
		JsonObject json = new JsonObject();
		json.addProperty("type", "LineString");
		json.add("coordinates", KmlGeometry.geoJSONCoordinates(mCoordinates));
		return json;
	}
	
	@Override public BoundingBoxE6 getBoundingBox(){
		if (mCoordinates!=null)
			return BoundingBoxE6.fromGeoPoints(mCoordinates);
		else 
			return null;
	}
	
	//Cloneable implementation ------------------------------------
	
	@Override public KmlLineString clone(){
		KmlLineString kmlLineString = (KmlLineString)super.clone();
		return kmlLineString;
	}
	
	//Parcelable implementation ------------
	
	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
	}
	
	public static final Parcelable.Creator<KmlLineString> CREATOR = new Parcelable.Creator<KmlLineString>() {
		@Override public KmlLineString createFromParcel(Parcel source) {
			return new KmlLineString(source);
		}
		@Override public KmlLineString[] newArray(int size) {
			return new KmlLineString[size];
		}
	};
	
	public KmlLineString(Parcel in){
		super(in);
	}
}
