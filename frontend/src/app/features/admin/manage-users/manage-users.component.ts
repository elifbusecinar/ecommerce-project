import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { User } from '../../../core/models/user.model';
import { UserService } from '../../../core/services/user.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule
  ],
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.scss']
})
export class ManageUsersComponent implements OnInit {
  users: User[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.userService.getAllUsers().subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.isLoading = false;
      },
      error: (err: any) => {
        this.errorMessage = 'Failed to load users. Please try again later.';
        console.error('Error loading users:', err);
        this.isLoading = false;
      }
    });
  }

  toggleUserStatus(user: User): void {
    const action = user.active ? this.userService.deactivateUser(user.id) : this.userService.activateUser(user.id);
    action.subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err: any) => {
        this.errorMessage = `Failed to ${user.active ? 'deactivate' : 'activate'} user.`;
        console.error('Error toggling user status:', err);
      }
    });
  }
}
