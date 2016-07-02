package com.example.souvik.remindertemplate.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class JSON {

    final static String GETACTUALJSON = "Prescription";
    final static String ID = "id";
    final static String PATIENT_ID = "patient_id";
    final static String PRESCRIPTION = "prescription";
    final static String TIMESTAMP = "timestamp";
    final static String DOCTORNAME = "Doctor_Name";

    /**
     * Gets the actual JSON you have to process on. Use this on the response
     * given by the REST API and then process the returned JSON object further
     *
     * @throws JSONException
     */

    public static JSONObject getActualPrescription(String JSONResponse)
            throws JSONException {

        JSONObject JSONtoProcess = new JSONObject(JSONResponse);

        try {
            JSONtoProcess = JSONtoProcess.getJSONObject(GETACTUALJSON);
        } catch (Exception e) {
            return JSONtoProcess;
        }

        return JSONtoProcess;

    }

    public static JSONArray getListOfAllPrescriptions(String JSONResponse)
            throws JSONException {

        JSONObject actualPresData = new JSONObject(JSONResponse);
        JSONArray presList = actualPresData.getJSONArray(GETACTUALJSON);

        return presList;
    }

    // for parsing list of prescriptions from api
    // Prescription/api/v1.0/prescription/3

    public static HashMap<String, JSONObject> returnMapofMedicine(
            String JSONResponse) throws JSONException {

        JSONObject actualJSON;


        actualJSON = getActualPrescription(JSONResponse);

        String prescriptionJSON = actualJSON.getString(PRESCRIPTION);
        JSONObject prescription = new JSONObject(prescriptionJSON);

        JSONObject medicine = prescription.getJSONObject("Medicines");
        // System.out.println(medicine.toString());

        HashMap<String, JSONObject> mapOfMedicineKey = jsonToMap(medicine);
        return mapOfMedicineKey;
    }


    public static List<String> returnListofMedicineNames(String JSONResponse)
            throws JSONException {

        HashMap<String, JSONObject> MapofMedicine;

        // try catch to handle if the json is from api all prescriptions or only
        // latest prescriptions

        // try{
        MapofMedicine = returnMapofMedicine(JSONResponse);
        // }catch(Exception e){
        // MapofMedicine = returnMapofMedicineAlternate(JSONResponse);
        // }
        List<String> medicineNames = new ArrayList<>();
        medicineNames.addAll(MapofMedicine.keySet());

        return medicineNames;
    }

    public static List<JSONObject> returnListofIndividualMedicineinfo(
            String JSONResponse) throws JSONException {

        HashMap<String, JSONObject> MapofMedicine = returnMapofMedicine(JSONResponse);
        List<JSONObject> medicineIndividualInfo = new ArrayList<>();

        for (Entry<String, JSONObject> entry : MapofMedicine.entrySet()) {
            // System.out.println(entry.getKey());
            // System.out.println(entry.getValue());
            medicineIndividualInfo.add(entry.getValue());
        }

        return medicineIndividualInfo;

    }

    public static List<JSONObject> returnMedicineInfoBasedOnTime(
            JSONObject jObject) throws JSONException {

        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if(key.equals("Total_Quantity")){
                continue;
            }
            JSONObject value = jObject.getJSONObject(key);
            map.put(key, value);

        }

        List<JSONObject> medicineIndividualInfo = new ArrayList<>();

        for (Entry<String, JSONObject> entry : map.entrySet()) {
            // System.out.println(entry.getKey());
            // System.out.println(entry.getValue());
            medicineIndividualInfo.add(entry.getValue());
        }
        return medicineIndividualInfo;
    }

    public static HashMap<String, Integer> chemistMedicineListHashMap(
            String JSONResponse) throws JSONException {

        HashMap<String, JSONObject> MapofMedicine = returnMapofMedicine(JSONResponse);
        HashMap<String, Integer> mapViewChemist = new HashMap<>();

        for (Entry<String, JSONObject> entry : MapofMedicine.entrySet()) {

            mapViewChemist.put(entry.getKey(),
                    extractQuantityfromMedicine(entry.getValue()));
        }

        return mapViewChemist;
    }




    public static int retreiveTOTAL_QUANTITY(String JSONResponse)
            throws JSONException {

        return 0;
    }

    public static int extractQuantityfromMedicine(JSONObject t)
            throws JSONException {
        return t.getInt("Total_Quantity");

    }

    public static HashMap<String, JSONObject> parseMedicines(JSONObject t)
            throws JSONException {

        HashMap<String, JSONObject> map = new HashMap<>();
        JSONObject jObject = t;
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {

            String key = (String) keys.next();

            try {
                JSONObject value = jObject.getJSONObject(key);
                map.put(key, value);
            } catch (Exception e) {
                continue;
            }

        }

        return map;
    }

    public static HashMap<String, JSONObject> jsonToMap(JSONObject t)
            throws JSONException {

        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
        JSONObject jObject = t;
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONObject value = jObject.getJSONObject(key);
            map.put(key, value);

        }

        return map;
    }

    public static String getNameofDoctor(String JSONResponse) throws JSONException {

        JSONObject actualJSON = getActualPrescription(JSONResponse);

        JSONObject patient_id = new JSONObject(actualJSON.getString(PRESCRIPTION));
        String doctorName = patient_id.getString(DOCTORNAME);
        System.out.println(doctorName);

        return doctorName;
    }

    public static Date getDateofPrescription(String JSONResponse) throws JSONException, ParseException {

        JSONObject actualJSON = getActualPrescription(JSONResponse);

//			JSONObject patient_id = new JSONObject(actualJSON.getString(PRESCRIPTION));
        String date = actualJSON.getString("timestamp");
//			System.out.println(date);

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date result1 = df1.parse(date);

        return result1;
    }

    public static String getIDofPrescription(String JSONResponse) throws JSONException {

        JSONObject actualJSON = getActualPrescription(JSONResponse);

//        JSONObject patient_id = new JSONObject(actualJSON.getString(PRESCRIPTION));
        String prescriptionID = Integer.toString(actualJSON.getInt("id"));

        return prescriptionID;
    }

    //keys = presID, DocName, presDate, medQuantity
    public static ArrayList<HashMap<String, Object>> getHashMapforPrescriptionList(String receivedJSONString)
            throws JSONException, ParseException {

        String LOG_TAG = "PrescriptionListMethod";
        ArrayList<HashMap<String, Object>> listviewPrescription = new ArrayList<>();

        JSONArray presList = JSON.getListOfAllPrescriptions(receivedJSONString);
        Log.v(LOG_TAG, Integer.toString(presList.length()));

        for (int i = 0; i < presList.length(); i++) {
            JSONObject prescriptionFromArr = presList.getJSONObject(i);

            String id = JSON.getIDofPrescription(prescriptionFromArr.toString());
            HashMap<String, Integer> mapMedQuant = JSON.chemistMedicineListHashMap(presList.getJSONObject(i).toString());
            String docName = JSON.getNameofDoctor(prescriptionFromArr.toString());
            Date presDate = JSON.getDateofPrescription(prescriptionFromArr.toString());

            HashMap<String, Object> listHashmap = new HashMap<>();

            listHashmap.put("presID", id);
            listHashmap.put("DocName", docName);
            listHashmap.put("presDate", presDate);
            listHashmap.put("medQuantity", mapMedQuant);
            listHashmap.put("jsonFile", prescriptionFromArr.toString());

            listviewPrescription.add(listHashmap);

            Log.v(LOG_TAG, "List len - " + listviewPrescription.size());
        }

        for (HashMap<String, Object> list : listviewPrescription) {

            String id = (String) list.get("presID");
            String docName1 = (String) list.get("DocName");
            Date timestamp = (Date) list.get("presDate");
            HashMap<String, Integer> mapMedQuant = (HashMap<String, Integer>) list.get("medQuantity");

//            Log.v(LOG_TAG, "Pres ID - " + id);
//            Log.v(LOG_TAG, "Doc name - " + docName1);
//            Log.v(LOG_TAG, "Pres date - " + timestamp.toString());
//            Log.v(LOG_TAG, "mapMedQuant size - " + mapMedQuant.size());

            for (Entry<String, Integer> entry : mapMedQuant.entrySet()) {
                String key = entry.getKey();
                String value = Integer.toString(entry.getValue());
                // do stuff
                Log.v(LOG_TAG, " med - " + key + " - quant - " + value);
            }

        }

        return listviewPrescription;
    }
}