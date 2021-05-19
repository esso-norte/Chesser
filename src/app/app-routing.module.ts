import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from "./auth/signup/signup.component";
import { LoginComponent } from "./auth/login/login.component";
import { GameComponent } from "./game/view-game/game.component";
import { UserListComponent } from "./user-list/user-list.component";
import { ProfilePageComponent } from "./profile/profile-page.component";

const routes: Routes = [
    { path: '', redirectTo: '/profile', pathMatch: 'full' },
    { path: 'sign-up', component: SignupComponent },
    { path: 'login', component: LoginComponent },
    { path: 'profile', component: ProfilePageComponent },
    { path: 'players', component: UserListComponent },
    { path: 'game/:gameId', component: GameComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
