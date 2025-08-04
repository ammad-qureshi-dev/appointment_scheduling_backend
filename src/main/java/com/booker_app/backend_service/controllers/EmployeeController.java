package com.booker_app.backend_service.controllers;

import com.booker_app.backend_service.controllers.request.AddEmployeeRequest;
import com.booker_app.backend_service.controllers.request.RemoveEmployeeRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.EmployeeDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final List<ResponseData> responseData = new ArrayList<>();

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/{companyId}")
    public ResponseEntity<ServiceResponse<UUID>> addEmployee(@PathVariable UUID companyId, @RequestBody AddEmployeeRequest request) {
        try {
            var userId = employeeService.addEmployeeToCompany(companyId, request);
            return getServiceResponse(true, userId, HttpStatus.CREATED);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ServiceResponse<List<EmployeeDTO>>> getAllEmployees(@PathVariable UUID companyId) {
        try {
            var result = employeeService.getAllEmployees(companyId);
            return getServiceResponse(true, result, HttpStatus.OK);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ServiceResponse<Boolean>> removeEmployee(@PathVariable UUID companyId, @RequestBody RemoveEmployeeRequest request) {
        try {
            var isDeleted = employeeService.removeEmployeeFromCompany(companyId, request);
            return getServiceResponse(true, isDeleted, HttpStatus.OK);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
    }

}
