import { Component, OnInit } from '@angular/core';
import {AuthService} from "../auth/shared/auth.service";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../shared/user.service";
import {UserModel} from "../shared/models/user-model";
import {faChess} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})

export class UserListComponent implements OnInit {

    faChess = faChess;

    username: string;
    users: UserModel[];

    constructor(private authService: AuthService, private activatedRoute: ActivatedRoute, private userService: UserService) { }

    ngOnInit(): void {
        this.username = this.authService.getUserName();

        this.userService.getAllUsers().subscribe(data => {
            this.users = data;
            console.log(this.users);
        });
    }

    challenge(user: UserModel): void {
        this.userService.challengeUser(user.id).subscribe(() => {
            user.challenged = true;
        })
    }

}
