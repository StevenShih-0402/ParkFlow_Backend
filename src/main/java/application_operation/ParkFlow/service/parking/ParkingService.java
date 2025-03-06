package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkinRequestCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestUpdateRq;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import application_operation.ParkFlow.dao.parking.ParkingDao;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ParkingDao parkingDao;
    private final JwtUtil jwtUtil;

    public ParkingRequestDto create (ParkinRequestCreateRq parkinRequestCreateRq) {

        jwtUtil.validateToken();

        if(!parkinRequestCreateRq.getCellPhone().matches("^\\d{10}$")){
            throw new HandleException("格式錯誤，電話號碼必須為10個數字。");
        }

        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        ParkingRequestCreateDto parkingRequestCreateDto = ParkingRequestCreateDto.builder()
                .weekStartDate(parkinRequestCreateRq.getWeekStartDate())
                .cellPhone(parkinRequestCreateRq.getCellPhone())
                .carNumber(parkinRequestCreateRq.getCarNumber())
                .carType(parkinRequestCreateRq.getCarType())
                .build();


        return parkingDao.saveParkingRequest(parkingRequestCreateDto, usersBaseDto);
    }

    public void update(ParkingRequestUpdateRq parkingRequestUpdateRq) {

        jwtUtil.validateToken();

        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        if(!usersBaseDto.getRoleName().equals("FM")) {
            throw new HandleException("Permission verification error.");
        }

        ParkingRequestUpdateDto parkingRequestUpdateDto = ParkingRequestUpdateDto
                .builder()
                .id(parkingRequestUpdateRq.getId())
                .status(parkingRequestUpdateRq.getStatus())
                .parkingSlotNumber(parkingRequestUpdateRq.getParkingSlotNumber())
                .build();

        parkingDao.updateParkingRequest(parkingRequestUpdateDto);
    }

}
