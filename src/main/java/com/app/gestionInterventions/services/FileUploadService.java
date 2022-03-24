package com.app.gestionInterventions.services;

import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.user.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {

    public static List loadMaterial(File file, Class<?> c) throws java.io.FileNotFoundException {
        JsonParser jsonParser;
        jsonParser = new JsonParser();
        JsonElement mapping = jsonParser.parse(new FileReader(file)).getAsJsonArray();
        Type listType;
        if (User.class.equals(c.getClass())) {
            listType = new TypeToken<List<User>>() {}.getType();
            return (List<User>) new Gson().fromJson(mapping, listType);
        }
        if (Material.class.equals(c.getClass())) {
            listType = new TypeToken<List<Material>>() {}.getType();
            return (List<Material>) new Gson().fromJson(mapping, listType);
        }
        return new ArrayList();
    }
}
