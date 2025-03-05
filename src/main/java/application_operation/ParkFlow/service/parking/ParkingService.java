package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkinRequestCreateRq;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import application_operation.ParkFlow.dao.parking.ParkingDao;

@RequiredArgsConstructor
@Service
public class ParkingService extends BaseService {

    private final ParkingDao parkingDao;

    public ParkingRequestDto create (ParkinRequestCreateRq parkinRequestCreateRq) {

        validateToken();

        UsersBaseDto usersBaseDto = getUserBase();

        ParkingRequestCreateDto parkingRequestCreateDto = ParkingRequestCreateDto.builder()
                .weekStartDate(parkinRequestCreateRq.getWeekStartDate())
                .cellPhone(parkinRequestCreateRq.getCellPhone())
                .carNumber(parkinRequestCreateRq.getCarNumber())
                .carType(parkinRequestCreateRq.getCarType())
                .build();


        return parkingDao.saveParkingRequest(parkingRequestCreateDto, usersBaseDto);
    }

}
