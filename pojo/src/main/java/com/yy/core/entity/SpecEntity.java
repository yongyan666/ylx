package com.yy.core.entity;

import com.yy.core.pojo.specification.Specification;
import com.yy.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;
/**
 * 规格组合实体类
 * @author Administrator
 *
 */

public class SpecEntity implements Serializable {
    //规格对象
    private Specification specification;
    //规格选项集合
    private List<SpecificationOption> specificationOptionList;
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
