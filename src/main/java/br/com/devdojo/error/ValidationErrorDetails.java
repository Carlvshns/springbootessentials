package br.com.devdojo.error;

public class ValidationErrorDetails extends ErrorDetails {
    
    private String field;
    private String fieldMessage;

    public void setField(String field){
        this.field = field;
    }
    public String getField(){
        return field;
    }

    public void setFieldMessage(String fieldMessage){
        this.fieldMessage = fieldMessage;
    }
    public String getFieldMessage(){
        return fieldMessage;
    }

    public static final class Builder{
        private String title;
        private int status;
        private String details;
        private long timestamp;
        private String developerMessage;
        private String field;
        private String fieldMessage;

        private Builder (){
        }

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder status(int status){
            this.status = status;
            return this;
        }

        public Builder details(String details){
            this.details = details;
            return this;
        }

        public Builder timestamp(long timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public Builder developerMessage(String developerMessage){
            this.developerMessage = developerMessage;
            return this;
        }

        public Builder field(String field){
            this.field = field;
            return this;
        }
        
        public Builder fieldMessage(String fieldMessage){
            this.fieldMessage = fieldMessage;
            return this;
        }

        public  ValidationErrorDetails build(){
            ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails();
            validationErrorDetails.setDeveloperMessage(developerMessage);
            validationErrorDetails.setTitle       (title);
            validationErrorDetails.setDetails      (details);
            validationErrorDetails.setTimestamp   (timestamp);
            validationErrorDetails.setStatus      (status);
            validationErrorDetails.field = field;
            validationErrorDetails.fieldMessage = fieldMessage;
            return validationErrorDetails;
        }
    }
}
