package group1.intern.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group1.intern.model.Constant;
import group1.intern.repository.ConstantRepository;
import group1.intern.service.ConstantService;

@Service
public class ConstantServiceImpl implements ConstantService {
    @Autowired
    private ConstantRepository constantRepository;

    @Override
    public List<Constant> getListConstantsByType(String type) {
        return constantRepository.findByType(type);
    }
}
