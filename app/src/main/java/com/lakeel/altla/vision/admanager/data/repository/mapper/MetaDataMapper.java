package com.lakeel.altla.vision.admanager.data.repository.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public final class MetadataMapper {

    private static final String KEY_UUID = "uuid";

    private static final String KEY_NAME = "name";

    private static final String KEY_DATE = "date";

    private static final String KEY_TRANSFORM_POSITION = "transformationPosition";

    private static final String KEY_TRANSFORM_ROTATION = "transformationRotation";

    public AreaDescriptionMetadata fromJson(String json) {
        if (json == null) throw new ArgumentNullException("json");

        try {
            JSONObject jsonObject = new JSONObject(json);

            AreaDescriptionMetadata metadata = new AreaDescriptionMetadata();

            metadata.uuid = jsonObject.getString(KEY_UUID);
            metadata.name = jsonObject.getString(KEY_NAME);
            metadata.date = new Date(jsonObject.getLong(KEY_DATE));

            JSONArray transformPosition = jsonObject.getJSONArray(KEY_TRANSFORM_POSITION);
            metadata.transformationPosition = new double[3];
            for (int i = 0; i < 3; i++) {
                metadata.transformationPosition[i] = transformPosition.getDouble(i);
            }

            JSONArray transformRotation = jsonObject.getJSONArray(KEY_TRANSFORM_ROTATION);
            metadata.transformationRotation = new double[4];
            for (int i = 0; i < 4; i++) {
                metadata.transformationRotation[i] = transformRotation.getDouble(i);
            }

            return metadata;
        } catch (JSONException e) {
            throw new IllegalStateException("Parsing JSON failed: json = " + json, e);
        }
    }

    public String toJson(AreaDescriptionMetadata metadata) {
        if (metadata == null) throw new ArgumentNullException("metadata");

        try {
            JSONObject jsonObject = new JSONObject().put(KEY_UUID, metadata.uuid)
                                                    .put(KEY_NAME, metadata.name)
                                                    .put(KEY_DATE, metadata.date.getTime());

            for (int i = 0; i < metadata.transformationPosition.length; i++) {
                jsonObject.accumulate(KEY_TRANSFORM_POSITION, metadata.transformationPosition[i]);
            }
            for (int i = 0; i < metadata.transformationRotation.length; i++) {
                jsonObject.accumulate(KEY_TRANSFORM_ROTATION, metadata.transformationRotation[i]);
            }

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Creating JSON failed: uuid = " + metadata.uuid, e);
        }
    }

    public AreaDescriptionMetadata fromTangoAreaDescriptionMetaData(TangoAreaDescriptionMetaData tangoMetaData) {
        if (tangoMetaData == null) throw new ArgumentNullException("tangoMetaData");

        AreaDescriptionMetadata metadata = new AreaDescriptionMetadata();

        metadata.uuid = TangoAreaDescriptionMetaDataHelper.getUuid(tangoMetaData);
        metadata.name = TangoAreaDescriptionMetaDataHelper.getName(tangoMetaData);
        metadata.date = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(tangoMetaData);

        double[] transformation = TangoAreaDescriptionMetaDataHelper.getTransformation(tangoMetaData);

        metadata.transformationPosition = new double[] {
                transformation[0],
                transformation[1],
                transformation[2]
        };
        metadata.transformationRotation = new double[] {
                transformation[3],
                transformation[4],
                transformation[5],
                transformation[6]
        };

        return metadata;
    }
}
