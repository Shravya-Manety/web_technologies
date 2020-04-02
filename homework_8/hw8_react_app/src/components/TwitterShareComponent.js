/* eslint-disable react/prop-types */
import React from 'react'
import {TwitterShareButton, TwitterIcon} from "react-share";

const TwitterShareComponent = (props) => (
    <div className = {props.className}>
        <TwitterShareButton
            url={props.newsUrl}
            hashtags={["CSCI_571_NewsApp"]}
            data-tip="Twitter"
            data-for ="tooltip-twitter"
            data-class="padding"
          >
            <TwitterIcon size={props.size} round />
          </TwitterShareButton>
    </div>
);

export default TwitterShareComponent