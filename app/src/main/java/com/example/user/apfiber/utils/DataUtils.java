package com.example.user.apfiber.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.example.user.apfiber.network.model.Pop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KT on 28/12/15.
 */
public class DataUtils {

    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_ZONAL = "key_zonal";
    public static final String KEY_DISTRICT = "key_distrcit";
    public static final String KEY_COUNTRY_CODE = "key_country_code";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_TOKEN = "key_token";
    public static final String KEY_STATUS = "key_status";
    public static final String KEY_AUTO_STATUS = "key_auto_status";
    public static final String KEY_ACTIVE = "key_active";
    public static final String USER_PREF = "user_pref";
    public static final String FORM_TEMPLATE = "form_template";
    public static final String FORM_TEMPLATE_KEY_NAME = "form_template_key_name";
    private static final String POLE_PREF = "pole_pref";
    private static final String POP_PREF = "pop_pref";
    private static final String FEASIBILE_PREF = "feasibile_pref";
    private static final String INSTALLATION_PREF = "installations_pref";
    private static final String POLE_IMG_PREF = "pole_img_pref";
    private static final String POP_IMG_PREF = "pop_img_pref";
    private static final String FEASIBILITY_IMG_PREF = "feasibility_img_pref";
    private static final String INSTALLATION_IMG_PREF = "installation_img_pref";
    private static final String DISTRICTS_PREF = "districts_pref";
    private static final String POPS_PREF = "pops_pref";
    private static SharedPreferences poleSharedPreferences, popsSharedPreferences;
    private static SharedPreferences feasibilitySharedPreferences, installationSharedPreferences;

    public static void saveName(Context mContext, String name) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public static void saveEmail(Context mContext, String email) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public static void saveMobile(Context mContext, String mobile) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MOBILE, mobile);
        editor.apply();
    }

    public static void savePassword(Context mContext, String password) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static void saveToken(Context mContext, String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static void saveCountryCode(Context mContext, String countryCode) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_COUNTRY_CODE, countryCode);
        editor.apply();
    }

    public static void saveFormTemplate(Context mContext, String formTemplateStr) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FORM_TEMPLATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FORM_TEMPLATE_KEY_NAME, formTemplateStr);
        editor.apply();
    }

    public static String getFormTemplate(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FORM_TEMPLATE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FORM_TEMPLATE_KEY_NAME, null);
    }

    public static JSONArray getFormFieldsForCategory(Context mContext, String category) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FORM_TEMPLATE, Context.MODE_PRIVATE);
        String formTemplate = sharedPreferences.getString(FORM_TEMPLATE_KEY_NAME, null);
        if (formTemplate != null) {
            try {
                JSONArray templateArray = new JSONArray(formTemplate);
                for (int i = 0; i < templateArray.length(); i++) {
                    String mCategory = templateArray.getJSONObject(i).getString("category");
                    if (mCategory != null && mCategory.equals(category)) {
                        return templateArray.getJSONObject(i).getJSONArray("fields");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getEmail(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public static String getName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public static String getMobileNumber(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILE, null);
    }

    public static String getPassword(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public static String getToken(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public static String getCountryCode(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_COUNTRY_CODE, null);
    }

    public static void saveStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, null);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    public static void saveAutoStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, status);
        editor.apply();
    }

    public static String getStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STATUS, null);
    }

    public static String getAutoStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTO_STATUS, null);
    }

    public static void setActive(Context mContext, boolean active) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ACTIVE, active);
        editor.apply();
    }

    public static boolean isActivated(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ACTIVE, false);
    }

    public static void saveZonal(Context mContext, String zonal) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ZONAL, zonal);
        editor.apply();
    }

    public static String getZonal(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ZONAL, null);
    }

    public static void saveDistrict(Context mContext, String district) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DISTRICT, district);
        editor.apply();
    }

    public static String getDistrict(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DISTRICT, null);
    }

    public static void savePole(Context mContext, JSONObject pole) {
        if (pole != null) {
            JSONArray polesArray = getPoles(mContext);
            if (polesArray == null) {
                polesArray = new JSONArray();
            }
            polesArray.put(pole);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(POLE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("poles", polesArray.toString());
            editor.apply();
        }
    }

    public static JSONArray getPoles(Context mContext) {
        poleSharedPreferences = mContext.getSharedPreferences(POLE_PREF, Context.MODE_PRIVATE);
        String featureJson = poleSharedPreferences.getString("poles", null);
        JSONArray polesArray = null;
        if (featureJson != null) {
            try {
                polesArray = new JSONArray(featureJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polesArray;
    }

    public static void clearPolePref() {
        if (poleSharedPreferences != null)
            poleSharedPreferences.edit().clear().commit();
    }

    public static void clearPopPref() {
        if (popsSharedPreferences != null)
            popsSharedPreferences.edit().clear().commit();
    }

    public static void clearFeasibilityPref() {
        if (feasibilitySharedPreferences != null)
            feasibilitySharedPreferences.edit().clear().commit();
    }

    public static void clearInstallationPref() {
        if (installationSharedPreferences != null)
            installationSharedPreferences.edit().clear().commit();
    }

    public static void savePop(Context mContext, JSONObject pop) {
        if (pop != null) {
            JSONArray popsArray = getPops(mContext);
            if (popsArray == null) {
                popsArray = new JSONArray();
            }
            popsArray.put(pop);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(POP_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pops", popsArray.toString());
            editor.apply();
        }
    }

    public static JSONArray getPops(Context mContext) {
        popsSharedPreferences = mContext.getSharedPreferences(POP_PREF, Context.MODE_PRIVATE);
        String featureJson = popsSharedPreferences.getString("pops", null);
        JSONArray popsArray = null;
        if (featureJson != null) {
            try {
                popsArray = new JSONArray(featureJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return popsArray;
    }

    public static void saveFeasibility(Context mContext, JSONObject feasible) {
        if (feasible != null) {
            JSONArray feasibleArray = getFeasibilities(mContext);
            if (feasibleArray == null) {
                feasibleArray = new JSONArray();
            }
            feasibleArray.put(feasible);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(FEASIBILE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("feasibles", feasibleArray.toString());
            editor.apply();
        }
    }

    public static JSONArray getFeasibilities(Context mContext) {
        feasibilitySharedPreferences = mContext.getSharedPreferences(FEASIBILE_PREF, Context.MODE_PRIVATE);
        String featureJson = feasibilitySharedPreferences.getString("feasibles", null);
        JSONArray feasibleArray = null;
        if (featureJson != null) {
            try {
                feasibleArray = new JSONArray(featureJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return feasibleArray;
    }

    public static void saveInstallation(Context mContext, JSONObject installation) {
        if (installation != null) {
            JSONArray installationArray = getInstallations(mContext);
            if (installationArray == null) {
                installationArray = new JSONArray();
            }
            installationArray.put(installation);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(INSTALLATION_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("installations", installationArray.toString());
            editor.apply();
        }
    }

    public static JSONArray getInstallations(Context mContext) {
        installationSharedPreferences = mContext.getSharedPreferences(INSTALLATION_PREF, Context.MODE_PRIVATE);
        String featureJson = installationSharedPreferences.getString("installations", null);
        JSONArray installationArray = null;
        if (featureJson != null) {
            try {
                installationArray = new JSONArray(featureJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return installationArray;
    }

    public static void savePoleImages(Context mContext, ArrayList<String> uri) {
        if (uri != null) {
            ArrayList<ArrayList<String>> poleImagesArray = getPoleImageArrayList(mContext);
            if (poleImagesArray == null) {
                poleImagesArray = new ArrayList<>();
            }
            poleImagesArray.add(uri);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(POLE_IMG_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(poleImagesArray);
            editor.putString("poleImages", json);
            editor.apply();
        }
    }

    public static ArrayList<ArrayList<String>> getPoleImageArrayList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(POLE_IMG_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("poleImages", null);
        ArrayList<ArrayList<String>> poleImagesArray = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
            }.getType();
            poleImagesArray = gson.fromJson(featureJson, type);
        }
        return poleImagesArray;
    }

    public static void savePopImages(Context mContext, ArrayList<String> uri) {
        if (uri != null) {
            ArrayList<ArrayList<String>> popImagesArray = getPopImageArrayList(mContext);
            if (popImagesArray == null) {
                popImagesArray = new ArrayList<>();
            }
            popImagesArray.add(uri);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(POP_IMG_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(popImagesArray);
            editor.putString("popImages", json);
            editor.apply();
        }
    }

    public static ArrayList<ArrayList<String>> getPopImageArrayList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(POP_IMG_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("popImages", null);
        ArrayList<ArrayList<String>> popImagesArray = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
            }.getType();
            popImagesArray = gson.fromJson(featureJson, type);
        }
        return popImagesArray;
    }

    public static void saveFeasibilityImages(Context mContext, ArrayList<String> uri) {
        if (uri != null) {
            ArrayList<ArrayList<String>> feasibilityImagesArray = getFeasibilityImageArrayList(mContext);
            if (feasibilityImagesArray == null) {
                feasibilityImagesArray = new ArrayList<>();
            }
            feasibilityImagesArray.add(uri);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(FEASIBILITY_IMG_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(feasibilityImagesArray);
            editor.putString("feasibilityImages", json);
            editor.apply();
        }
    }

    public static ArrayList<ArrayList<String>> getFeasibilityImageArrayList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FEASIBILITY_IMG_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("feasibilityImages", null);
        ArrayList<ArrayList<String>> feasibilityImagesArray = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
            }.getType();
            feasibilityImagesArray = gson.fromJson(featureJson, type);
        }
        return feasibilityImagesArray;
    }

    public static void saveInstallationImages(Context mContext, ArrayList<String> uri) {
        if (uri != null) {
            ArrayList<ArrayList<String>> installationImagesArray = getInstallationImageArrayList(mContext);
            if (installationImagesArray == null) {
                installationImagesArray = new ArrayList<>();
            }
            installationImagesArray.add(uri);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(INSTALLATION_IMG_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(installationImagesArray);
            editor.putString("installationImages", json);
            editor.apply();
        }
    }

    public static ArrayList<ArrayList<String>> getInstallationImageArrayList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(INSTALLATION_IMG_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("installationImages", null);
        ArrayList<ArrayList<String>> installationImagesArray = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
            }.getType();
            installationImagesArray = gson.fromJson(featureJson, type);
        }
        return installationImagesArray;
    }

    public static void saveDistricts(Context mContext, ArrayList<Pop> districts) {
        if (districts != null) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(DISTRICTS_PREF, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(districts);
            editor.putString("districts", json);
            editor.apply();
        }
    }

    public static ArrayList<Pop> getDistrictList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(DISTRICTS_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("districts", null);
        ArrayList<Pop> districtsArray = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<Pop>>() {
            }.getType();
            districtsArray = gson.fromJson(featureJson, type);
        }
        return districtsArray;
    }

    public static void savePops(Context mContext, ArrayList<Pop> popsList) {
        if (popsList != null) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(POPS_PREF, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(popsList);
            editor.putString("popList", json);
            editor.apply();
        }
    }

    public static ArrayList<Pop> getPopsList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(POPS_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String featureJson = sharedPreferences.getString("popList", null);
        ArrayList<Pop> popList = null;
        if (featureJson != null) {
            Type type = new TypeToken<ArrayList<Pop>>() {
            }.getType();
            popList = gson.fromJson(featureJson, type);
        }
        return popList;
    }
}
