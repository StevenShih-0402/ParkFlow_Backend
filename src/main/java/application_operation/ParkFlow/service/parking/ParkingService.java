package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkinRequestCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestUpdateRq;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.RoleNameEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import application_operation.ParkFlow.dao.parking.ParkingDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ParkingDao parkingDao;
    private final JwtUtil jwtUtil;

    public ParkingRequestDto create (ParkinRequestCreateRq parkinRequestCreateRq) {

        jwtUtil.validateToken();

        if(!parkinRequestCreateRq.getCellPhone().matches("^\\d{10}$")){
            throw new HandleException("Invalid format: The phone number must be 10 digits.");
        }

        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        ParkingRequestCreateDto parkingRequestCreateDto = ParkingRequestCreateDto.builder()
                .weekStartDate(parkinRequestCreateRq.getWeekStartDate())
                .cellPhone(parkinRequestCreateRq.getCellPhone())
                .carNumber(parkinRequestCreateRq.getCarNumber())
                .carType(parkinRequestCreateRq.getCarType())
                .build();


        if(!parkingDao.findParkingRequestByApplicantId(parkingRequestCreateDto, usersBaseDto)) {
            throw new HandleException("Duplicate Application.");
        }

        if(!parkingDao.findParkingRequestCheckQuota(parkingRequestCreateDto)) {
            throw new HandleException("Application limit reached.");
        }

        return parkingDao.saveParkingRequest(parkingRequestCreateDto, usersBaseDto);
    }

    public UpdateParkingRequestDto update(ParkingRequestUpdateRq parkingRequestUpdateRq) {

        jwtUtil.validateToken();

        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        if(!usersBaseDto.getRoleName().equals(RoleNameEnum.FM.name())) {
            throw new HandleException("Permission verification error.");
        }

        ParkingRequestUpdateDto parkingRequestUpdateDto = ParkingRequestUpdateDto
                .builder()
                .id(parkingRequestUpdateRq.getId())
                .status(parkingRequestUpdateRq.getStatus())
                .parkingSlotNumber(parkingRequestUpdateRq.getParkingSlotNumber())
                .build();

        List<ParkingRequestEntity> parkingRequestEntities = parkingDao.findParkingRequestById(parkingRequestUpdateDto);

        return parkingDao.updateParkingRequest(parkingRequestEntities.get(0), parkingRequestUpdateDto, usersBaseDto);
    }

}
