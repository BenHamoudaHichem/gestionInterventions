package com.app.gestionInterventions.services;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.EMeasure;
import com.app.gestionInterventions.models.additional.Location;
import com.app.gestionInterventions.models.additional.QuantityValue;
import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class FileUploadService {
    @Autowired
    RoleRepository roleRepository;

    public  <T extends Object> List<T> serialize(File file, Class<T> t) throws FileNotFoundException {
        switch (FileNameUtils.getExtension(file.getName()))
        {
            case "json":
                return (List<T>) loadFileJsonObject(file,t);
            case "csv":
                return (List<T>) loadFileCsvObject(file, t);
            case "txt":
                return (List<T>) loadFileTxtObject(file,t);
            default:return new ArrayList<>();
        }
    }
    private List<? extends Object>  loadFileJsonObject(File file, Class<?> c) throws java.io.FileNotFoundException {
        JsonParser jsonParser;
        jsonParser = new JsonParser();
        JsonElement mapping = jsonParser.parse(new FileReader(file)).getAsJsonArray();
        Type listType;
        if (User.class.equals(c.getClass())) {
            listType = new TypeToken<List<User>>() {}.getType();
            return (List<Material>) new Gson().fromJson(mapping, listType);
        }
        if (Material.class.equals(c.getClass())) {
            listType = new TypeToken<List<Material>>() {}.getType();
            return (List<User>) new Gson().fromJson(mapping, listType);
        }
        return null;
    }
    private List<? extends Object>  loadFileCsvObject(File file, Class<?> c) {
        List<?> records = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            scanner.useDelimiter("\n");
            scanner.next();
            while (scanner.hasNext()) {
                if (Material.class==c) {
                    ((List<Material>) records).add(createMaterialfromArray(scanner.next().split(",")));
                }
                if (c == User.class) {
                    ((List<User>) records).add(createUserfromArray(scanner.next().split(",")));
                }
            }
        } catch (FileNotFoundException e) {

            scanner.close();
            return null;
        }
        scanner.close();
        return records;

    }
    private List<? extends Object> loadFileTxtObject(File file, Class<?> c) {
        List<? extends Object> records = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            scanner.useDelimiter("\n");
            scanner.next();
            while (scanner.hasNext()) {
                if (Material.class.equals(c.getClass())) {
                    ((List<Material>) records).add(createMaterialfromArray(scanner.next().split("\t")));
                }
                if (User.class.equals(c.getClass())) {
                    System.out.println("eeee");
                    ((List<User>) records).add(createUserfromArray(scanner.next().split("\t")));
                }
            }
        } catch (FileNotFoundException e) {

            scanner.close();
            return null;
        }
        scanner.close();
        return records;
    }
    private static Material createMaterialfromArray(String[] data)
    {
        try {
            return new Material(
                    data[0],
                    data[1],
                    data[2],
                    new QuantityValue(Float.valueOf(data[3]), EMeasure.valueOf(data[4])),
                    new SimpleDateFormat("dd/MM/yyyy").parse(data[5]),
                    new Address(data[6],data[7],data[8],data[9],data[10],new Location(BigDecimal.valueOf(Double.valueOf(data[11])),BigDecimal.valueOf(Double.valueOf(data[12])))),
                    ECategory.valueOf(data[13]),
                    Status.valueOf(data[14])
            );
        } catch (ParseException e) {

            return null;
        }
    }
    private User createUserfromArray(String[] data)
    {
        User user= new User(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                new Address(data[5],data[6],data[7],data[8],data[9],new Location(BigDecimal.valueOf(Double.valueOf(data[10])),BigDecimal.valueOf(Double.valueOf(data[11])))),
                data[12]
        );
        if (Arrays.asList(data).subList(12, data.length-1).isEmpty()) {

            return  user;
        }
        user.setRoles(Arrays.asList(data).subList(12, data.length-1).stream().map(x->
                roleRepository.findByName(ERole.valueOf(x)).orElse(null)).collect(Collectors.toSet()));
            return  user;

    }


}
