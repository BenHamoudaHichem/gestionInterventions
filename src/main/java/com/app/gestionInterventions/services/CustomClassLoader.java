package com.app.gestionInterventions.services;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.stereotype.Service;
@Service
public class CustomClassLoader extends ClassLoader{
    @Override
    public Class findClass(String name)  {
        return new Reflections("com.app.gestionInterventions.models", new SubTypesScanner(false))
                .getSubTypesOf(Object.class)
                .stream().filter(
                        x->(!(x.getPackage().getName().endsWith("tools")||x.getPackage().getName().endsWith("additional")))&&x.getName().endsWith(name)
                ).findFirst().get();
    }
}
