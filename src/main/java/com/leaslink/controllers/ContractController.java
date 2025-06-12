package com.leaslink.controllers;

import com.leaslink.models.FinancingContract;
import com.leaslink.utils.DatabaseUtil;

import java.util.List;

public class ContractController {
    public List<FinancingContract> getContractsByDebtorNik(String nik) {
        return DatabaseUtil.getContractsByDebtorNik(nik);
    }

    public FinancingContract getContractDetail(String contractId, String nik) {
        List<FinancingContract> contracts = getContractsByDebtorNik(nik);
        for (FinancingContract fc : contracts) {
            if (fc.getId().equals(contractId)) {
                return fc;
            }
        }
        return null;
    }

    public List<FinancingContract> getAllContracts() {
        return DatabaseUtil.getAllContracts();
    }
    
    public List<FinancingContract> searchContractsByNik(String keyword) {
        return DatabaseUtil.searchContractsByNik(keyword);
    }
}
