package com.weather.report.repositories;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Operator;

public class OperatorRepository extends CRUDRepository<Operator, String> {

    public OperatorRepository() {
        super(Operator.class);
    }

    public void operatorExists(String email) throws InvalidInputDataException, IdAlreadyInUseException{
        if(email == null){ throw new InvalidInputDataException("Mandatory data missing : email!");}
        Operator out = this.read(email);
        if(out != null){ throw new IdAlreadyInUseException("Email already in use!");}
    }

    public Operator checkOperator(String email) throws InvalidInputDataException, ElementNotFoundException{
        if(email == null){ 
            throw new InvalidInputDataException("Mandatory data missing : email!");
        }
        Operator out = this.read(email);
        if(out == null){ 
            throw new ElementNotFoundException("User with email "+email+" not found!"); 
        }
        return out;
    }

}
