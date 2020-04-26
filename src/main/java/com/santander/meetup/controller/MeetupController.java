package com.santander.meetup.controller;

import com.santander.meetup.constant.MeetupEndpoint;
import com.santander.meetup.dto.request.MeetupCreationDto;
import com.santander.meetup.dto.response.MeetupDto;
import com.santander.meetup.exceptions.DuplicateEntityException;
import com.santander.meetup.exceptions.EntityNotFoundException;
import com.santander.meetup.service.MeetupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Meetups")
@RestController
@RequestMapping(value = MeetupEndpoint.ROOT)
public class MeetupController {

    @Autowired
    MeetupService meetupService;

    /**
     * Creates a new meetup.
     *
     * @param meetupCreationDto the meetup creation request body.
     * @return the created meetup.
     * @throws DuplicateEntityException if the meetup already exists.
     * @throws EntityNotFoundException  if the given owner user wasn't found.
     */
    @ApiOperation(value = "Creates a new meetup")
    @PostMapping()
    public ResponseEntity<MeetupDto> create(@Valid @RequestBody MeetupCreationDto meetupCreationDto) throws DuplicateEntityException, EntityNotFoundException {
        return new ResponseEntity<>(meetupService.create(meetupCreationDto), HttpStatus.CREATED);
    }

    /**
     * Retrieves the amount of beer cases needed for the given meetup.
     *
     * @param meetupId the meetup id from which the number of beer cases needed will be calculated.
     * @return the amount of beer cases needed.
     * @throws EntityNotFoundException if the given meetup wasn't found.
     */
    @ApiOperation(value = "Retrieves the amount of beer cases needed for the given meetup")
    @GetMapping(MeetupEndpoint.BEER_CASES)
    public ResponseEntity<Integer> getNeededBeerCases(@Valid @PathVariable long meetupId) throws EntityNotFoundException {
        return new ResponseEntity<>(meetupService.calculateNeededBeerCases(meetupId), HttpStatus.OK);
    }

    /**
     * Retrieves the day's temperature of the meetup.
     *
     * @param meetupId the meetup id from which the day's temperature will be retrieved.
     * @return the day's temperature of the meetup.
     * @throws EntityNotFoundException if the given meetup wasn't found.
     */
    @ApiOperation(value = "Retrieves the day's temperature of the meetup")
    @GetMapping(MeetupEndpoint.TEMPERATURE)
    public ResponseEntity<Double> getTemperature(@Valid @PathVariable long meetupId) throws EntityNotFoundException {
        return new ResponseEntity<>(meetupService.getTemperature(meetupId), HttpStatus.OK);
    }

    /**
     * Retrieves the meetups created by the given user.
     *
     * @param ownerId the owner id of the meetups to retrieve.
     * @return a list of the meetups created by the given user.
     */
    @ApiOperation(value = "Retrieves the meetups created by the given user")
    @GetMapping(MeetupEndpoint.CREATED)
    public ResponseEntity<List<MeetupDto>> getCreatedMeetups(@Valid @RequestParam long ownerId) {
        return new ResponseEntity<>(meetupService.getCreatedMeetups(ownerId), HttpStatus.OK);
    }

    /**
     * Retrieves the meetups to which the user is enrolled to.
     *
     * @param userId the user id of the meetups to retrieve.
     * @return a list of meetups to which the user is enrolled to.
     */
    @ApiOperation(value = "Retrieves the meetups to which the user is enrolled to")
    @GetMapping(MeetupEndpoint.ENROLLED)
    public ResponseEntity<List<MeetupDto>> getEnrolledMeetups(@Valid @RequestParam long userId) {
        return new ResponseEntity<>(meetupService.getEnrolledMeetups(userId), HttpStatus.OK);
    }
}
