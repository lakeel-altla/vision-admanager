package com.lakeel.altla.vision.admanager.data.repository.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public final class MetaDataMapper {

    private static final String KEY_UUID = "uuid";

    private static final String KEY_NAME = "name";

    private static final String KEY_DATE = "date";

    private static final String KEY_TRANSFORM_POSITION = "transformationPosition";

    private static final String KEY_TRANSFORM_ROTATION = "transformationRotation";

    public AreaDescriptionMetaData fromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            AreaDescriptionMetaData metaData = new AreaDescriptionMetaData();

            metaData.uuid = jsonObject.getString(KEY_UUID);
            metaData.name = jsonObject.getString(KEY_NAME);
            metaData.date = new Date(jsonObject.getLong(KEY_DATE));

            JSONArray transformPosition = jsonObject.getJSONArray(KEY_TRANSFORM_POSITION);
            metaData.transformationPosition = new double[3];
            for (int i = 0; i < 3; i++) {
                metaData.transformationPosition[i] = transformPosition.getDouble(i);
            }

            JSONArray transformRotation = jsonObject.getJSONArray(KEY_TRANSFORM_ROTATION);
            metaData.transformationRotation = new double[4];
            for (int i = 0; i < 4; i++) {
                metaData.transformationRotation[i] = transformRotation.getDouble(i);
            }

            return metaData;
        } catch (JSONException e) {
            throw new IllegalStateException("Parsing JSON failed: json = " + json, e);
        }
    }

    public String toJson(AreaDescriptionMetaData metaData) {
        try {
            JSONObject jsonObject = new JSONObject().put(KEY_UUID, metaData.uuid)
                                                    .put(KEY_NAME, metaData.name)
                                                    .put(KEY_DATE, metaData.date.getTime());

            for (int i = 0; i < metaData.transformationPosition.length; i++) {
                jsonObject.accumulate(KEY_TRANSFORM_POSITION, metaData.transformationPosition[i]);
            }
            for (int i = 0; i < metaData.transformationRotation.length; i++) {
                jsonObject.accumulate(KEY_TRANSFORM_ROTATION, metaData.transformationRotation[i]);
            }

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Creating JSON failed: uuid = " + metaData.uuid, e);
        }
    }

    public AreaDescriptionMetaData fromTangoAreaDescriptionMetaData(TangoAreaDescriptionMetaData tangoMetaData) {

        AreaDescriptionMetaData metaData = new AreaDescriptionMetaData();

        metaData.uuid = TangoAreaDescriptionMetaDataHelper.getUuid(tangoMetaData);
        metaData.name = TangoAreaDescriptionMetaDataHelper.getName(tangoMetaData);
        metaData.date = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(tangoMetaData);

        double[] transformation = TangoAreaDescriptionMetaDataHelper.getTransformation(tangoMetaData);

        metaData.transformationPosition = new double[] {
                transformation[0],
                transformation[1],
                transformation[2]
        };
        metaData.transformationRotation = new double[] {
                transformation[3],
                transformation[4],
                transformation[5],
                transformation[6]
        };

        return metaData;
    }
}
