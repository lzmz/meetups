package io.github.lzmz.meetups.controller;

import io.github.lzmz.meetups.dto.request.MeetupCreationDto;
import io.github.lzmz.meetups.dto.response.InvitationDto;
import io.github.lzmz.meetups.dto.response.MeetupAdminDto;
import io.github.lzmz.meetups.endpoint.MeetupEndpoint;
import io.github.lzmz.meetups.exceptions.DuplicateEntityException;
import io.github.lzmz.meetups.exceptions.EntityNotFoundException;
import io.github.lzmz.meetups.service.InvitationService;
import io.github.lzmz.meetups.service.MeetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Meetups")
@RestController
@RequestMapping(value = MeetupEndpoint.BASE)
public class MeetupController {

    private final MeetupService meetupService;
    private final InvitationService invitationService;

    public MeetupController(MeetupService meetupService, InvitationService invitationService) {
        this.meetupService = meetupService;
        this.invitationService = invitationService;
    }

    /**
     * Creates a new meetup.
     *
     * @param meetupCreationDto the meetup creation request body.
     * @return the created meetup.
     * @throws DuplicateEntityException if the meetup already exists.
     * @throws EntityNotFoundException  if the given owner user wasn't found.
     */
    @Operation(summary = "Creates a new meetup")
    @PostMapping()
    public ResponseEntity<MeetupAdminDto> create(@Valid @RequestBody MeetupCreationDto meetupCreationDto) throws DuplicateEntityException, EntityNotFoundException {
        return new ResponseEntity<>(meetupService.create(meetupCreationDto), HttpStatus.CREATED);
    }

    /**
     * Creates a list of new invitations for the given meetup.
     *
     * @param meetupId the meetup id to which the invitations will be sent.
     * @param userIds  a list with the user's ids that will be invited.
     * @return the created invitations.
     * @throws DuplicateEntityException if one of the given invitations already exists.
     * @throws EntityNotFoundException  if the given meetup or user wasn't found.
     */
    @Operation(summary = "Creates a new list of invitations for the given meetup")
    @PostMapping(MeetupEndpoint.INVITATIONS)
    public ResponseEntity<List<InvitationDto>> create(@Valid @PathVariable long meetupId,
                                                      @Valid @RequestBody List<Long> userIds) throws DuplicateEntityException, EntityNotFoundException {
        return new ResponseEntity<>(invitationService.create(meetupId, userIds), HttpStatus.CREATED);
    }

    /**
     * Retrieves the number of beer cases needed for the given meetup.
     *
     * @param meetupId the meetup id from which the number of beer cases needed will be calculated.
     * @return the amount of beer cases needed.
     * @throws EntityNotFoundException if the given meetup wasn't found.
     */
    @Operation(summary = "Retrieves the number of beer cases needed for the given meetup")
    @GetMapping(MeetupEndpoint.BEER_CASES)
    public ResponseEntity<Integer> getNeededBeerCases(@Valid @PathVariable long meetupId) throws EntityNotFoundException {
        return new ResponseEntity<>(meetupService.calculateBeerCasesNeeded(meetupId), HttpStatus.OK);
    }

    /**
     * Retrieves the day's temperature of the meetup.
     *
     * @param meetupId the meetup id from which the day's temperature will be retrieved.
     * @return the day's temperature of the meetup.
     * @throws EntityNotFoundException if the given meetup wasn't found.
     */
    @Operation(summary = "Retrieves the day's temperature of the meetup")
    @GetMapping(MeetupEndpoint.TEMPERATURE)
    public ResponseEntity<Double> getTemperature(@Valid @PathVariable long meetupId) throws EntityNotFoundException {
        return new ResponseEntity<>(meetupService.getTemperature(meetupId), HttpStatus.OK);
    }
}
