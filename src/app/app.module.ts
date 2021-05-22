import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './header/header.component';
import { ReactiveFormsModule } from "@angular/forms";
import { NgxWebstorageModule } from 'ngx-webstorage';
import { ToastrModule } from 'ngx-toastr';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from "@angular/router";
import { TokenInterceptor } from './token-interceptor';

import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { ChartModule } from 'angular2-chartjs';

import { GameComponent } from './game/view-game/game.component';
import { SignupComponent } from './auth/signup/signup.component';
import { LoginComponent } from './auth/login/login.component';
import { UserProfileComponent } from './auth/user-profile/user-profile.component';
import { UserListComponent } from './user-list/user-list.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { RatingHistoryComponent } from './profile-page/rating-history/rating-history.component';
import { StatsComponent } from './profile-page/stats/stats.component';
import { GamesListComponent } from './profile-page/games-list/games-list.component';
import { ChallengesListComponent } from './profile-page/challenges-list/challenges-list.component';

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,

        SignupComponent,
        LoginComponent,
        UserProfileComponent,
        GameComponent,
        UserListComponent,
        ProfilePageComponent,
        RatingHistoryComponent,
        StatsComponent,
        GamesListComponent,
        ChallengesListComponent,
    ],
    imports: [
        AppRoutingModule,
        BrowserModule,
        HttpClientModule,
        ReactiveFormsModule,
        NgxWebstorageModule.forRoot(),
        ToastrModule.forRoot(),
        FontAwesomeModule,
        RouterModule,
        MDBBootstrapModule.forRoot(),
        ChartModule
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
