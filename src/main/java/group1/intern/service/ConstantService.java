package group1.intern.service;

import java.util.List;

import group1.intern.model.Constant;

public interface ConstantService {
    List<Constant> getListConstantsByType(String type);
}
