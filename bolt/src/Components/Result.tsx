import React from "react";
import resultType from "../types/resultType";

type Props = {
	res: resultType;
	paragraph: string;
};

export default function Result({ res, paragraph }: Props) {
	return (
		<div className=" flex flex-col gap-1 items-start">
			<a href={res.url}>
				<h3 className=" font-bold text-lg hover:underline text-highlight">
					{res.title}
				</h3>
			</a>
			<a className=" text-neutral-500 " href={res.url}>
				{res.url}
			</a>
			{paragraph && (
				<p className=" text-neutral-600 ">
					{paragraph.split(" ").map((word, index) => {
						if (index == res.wIdx)
							return (
								<span className=" text-3xl font-bold underline text-highlight">
									{" "}
									{word}{" "}
								</span>
							);
						return word + " ";
					})}
				</p>
			)}
		</div>
	);
}
