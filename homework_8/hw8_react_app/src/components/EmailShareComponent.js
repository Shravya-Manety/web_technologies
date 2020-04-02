/* eslint-disable react/prop-types */
import React from 'react'
import { EmailShareButton, EmailIcon} from "react-share";

const EmailShareComponent = (props) => (
    <div className = {props.className}>
        <EmailShareButton
            url={props.newsUrl}
            subject={"#CSCI_571_NewsApp"}
            body={props.newsUrl}
            data-tip="Email"
            data-for ="tooltip-email"
            data-class="padding"
          >
            <EmailIcon size={props.size} round />
          </EmailShareButton>
    </div>
);

export default EmailShareComponent