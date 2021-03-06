import { InvitationStatus } from '../../enums/invitation-status';
import { Model } from '../model';

export interface IInvitationResponse extends Model {
  userId: number;
  meetupId: number;
  meetupOwnerName: string;
  meetupOwnerEmail: string;
  meetupDay: Date;
  meetupTemperature: number;
  status: InvitationStatus;
}
