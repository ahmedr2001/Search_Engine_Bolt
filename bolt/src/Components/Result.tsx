import React from "react";

type Props = {
	url: string;
};

export default function Result({ url }: Props) {
	return (
		<a className=" underline" href={url}>
			{url}
		</a>
	);
}
