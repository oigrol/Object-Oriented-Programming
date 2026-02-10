package com.weather.report.repositories;

import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.User;

public class UserRepository extends CRUDRepository<User, String>{

    public UserRepository() {
        super(User.class);
    }

    /**
     * Checks:
     *  - username not null
     *  - user in repository
     * Retrieve user if it exists, otherwise throw an exception
     * @param username    user performing the action
     * @return checked User
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws UnauthorizedException     when user is missing
     */
    public User check(String userCode) throws InvalidInputDataException, UnauthorizedException{
        User out = null;
        if(userCode == null){
            throw new InvalidInputDataException("Mandatory data are missing");
        }
        out = this.read(userCode);
        if(out == null){
            throw new UnauthorizedException("User not found");
        }
        return out;
    }

    /**
     * Checks:
     *  - username not null
     *  - user in repository 
     *  - user is {@code UserType.MAINTAINER}
     * @param username    user performing the action(mandatory, must be a
     *                    {@code UserType.MAINTAINER})
     * @return checked User
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws UnauthorizedException     when user is missing or not authorized
     */
    public User checkMaintainer(String userCode) throws InvalidInputDataException, UnauthorizedException{
        User out = this.check(userCode);
        if(out.getType() != UserType.MAINTAINER){
            throw new UnauthorizedException("User not authorized");
        }
        return out;
    }

}
