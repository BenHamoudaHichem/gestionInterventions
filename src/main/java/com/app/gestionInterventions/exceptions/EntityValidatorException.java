package com.app.gestionInterventions.exceptions;

public class EntityValidatorException extends  Exception{
    public EntityValidatorException() {
        super("Erreur au niveau de la stricture de l'entité !");
    }

    public EntityValidatorException(String message) {
        super(message);
    }

    public EntityValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityValidatorException(Throwable cause) {
        super(cause);
    }

    public EntityValidatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
